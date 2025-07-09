from flask_openapi3 import APIBlueprint, Tag

from dto.common_query_params import PaginationQueryParams
from dto.photo_dto import PhotoDetailsDTO
from dto.photo_metadata_dto import PhotoMetadataDTO
from dto.photo_update_metadata_dto import PhotoUpdateMetadataDTO
from dto.photo_route_dto import (
    PhotoIdPath,
    PhotoThumbnailRequest,
    PhotoThumbnailListResponse
)
from middleware.auth import jwt_required
from services.photo_manager import PhotoManager
from utils.request_utils import RequestParser
from validators.photo_validator import PhotoValidator


def create_photo_routes(photo_manager: PhotoManager, photo_validator: PhotoValidator) -> APIBlueprint:
    photo_tag = Tag(name="photo", description="Photo operations")
    photo_bp = APIBlueprint('photo', __name__, url_prefix='/api/photo')

    @photo_bp.get('/<int:photo_id>', responses={200: PhotoDetailsDTO}, tags=[photo_tag])
    def get_photo(path: PhotoIdPath):
        photo_details = photo_manager.get_photo_details(path.photo_id)
        return photo_details.model_dump()

    @photo_bp.put('/<int:photo_id>', tags=[photo_tag])
    @jwt_required
    def update_photo(path: PhotoIdPath):
        metadata_dict = RequestParser.get_json_from_multipart('metadata')
        files = RequestParser.get_files_list_optional('files')
        metadata = PhotoUpdateMetadataDTO.model_validate(metadata_dict)
        photo_validator.validate_update_request(path.photo_id, metadata, files)
        updated_photo = photo_manager.update_photo(path.photo_id, metadata, files)
        return updated_photo.model_dump()

    @photo_bp.delete('/<int:photo_id>', tags=[photo_tag])
    @jwt_required
    def delete_photo(path: PhotoIdPath):
        photo_validator.validate_delete_request(path.photo_id)
        photo_manager.delete_photo(path.photo_id)
        return '', 204

    @photo_bp.post('', tags=[photo_tag])
    @jwt_required
    def create_photo():
        metadata_dict = RequestParser.get_json_from_multipart('metadata')
        files = RequestParser.get_files_list('files')
        skip_validation = RequestParser.get_query_param('similarValidationSkip', bool, False)
        metadata = PhotoMetadataDTO.model_validate(metadata_dict)
        photo_validator.validate_create_request(metadata, files)
        if not skip_validation:
            photo_validator.validate_on_duplicates(files)
        new_photo = photo_manager.upload_photo(metadata, files)
        return new_photo.model_dump(), 201

    @photo_bp.get('/all', tags=[photo_tag])
    def get_all_photos(query: PaginationQueryParams):
        photos_page = photo_manager.get_all(query.page, query.size)
        return photos_page.model_dump()

    @photo_bp.get('/all/sitemap-data', tags=[photo_tag])
    def get_sitemap_data(query: PaginationQueryParams):
        sitemap_page = photo_manager.get_all_for_sitemap(query.page, query.size)
        return sitemap_page.model_dump()

    @photo_bp.post('/thumbnails/query', responses={200: PhotoThumbnailListResponse}, tags=[photo_tag])
    def get_photo_thumbnails(body: PhotoThumbnailRequest):
        photo_ids = body.photo_ids
        photo_validator.validate_get_photo_icons_request(photo_ids)
        thumbnails = photo_manager.get_thumbnail_urls_by_photo_ids(photo_ids)
        response = PhotoThumbnailListResponse.from_thumbnails(thumbnails)
        return response.model_dump()

    return photo_bp
