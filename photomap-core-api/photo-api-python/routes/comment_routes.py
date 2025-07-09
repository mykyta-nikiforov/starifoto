from flask import request, current_app
from flask_openapi3 import APIBlueprint, Tag

from services.comment_service import CommentService
from validators.comment_validator import CommentValidator
from mappers.comment_mapper import CommentMapper
from dto.comment_dto import (
    CommentDTO, 
    CreateCommentDTO, 
    PhotoIdPath, 
    PhotoCommentPath
)
from dto.common_query_params import PaginationQueryParams
from dto.page import Page
from middleware.auth import jwt_required


def create_comment_routes(comment_service: CommentService, comment_validator: CommentValidator, comment_mapper: CommentMapper) -> APIBlueprint:

    comment_tag = Tag(name="Comment API", description="API to work with comments")
    comment_bp = APIBlueprint('comment', __name__, url_prefix='/api/photo')
    
    @comment_bp.get('/<int:photo_id>/comment', responses={200: Page[CommentDTO]}, tags=[comment_tag])
    def get_comments(path: PhotoIdPath, query: PaginationQueryParams):
        current_app.logger.info(f"Getting comments for photo {path.photo_id}, page={query.page}, size={query.size}")
        comments_page = comment_service.find_all_by_photo_id(path.photo_id, query.page, query.size)
        comment_dtos_page = comment_mapper.convert_page_to_dto(comments_page)
        return comment_dtos_page.model_dump()
    
    @comment_bp.post('/<int:photo_id>/comment', responses={200: CommentDTO}, tags=[comment_tag])
    @jwt_required
    def create_comment(path: PhotoIdPath, body: CreateCommentDTO):
        current_app.logger.info(f"Creating comment for photo {path.photo_id}")
        created_comment = comment_service.create(path.photo_id, body.text)
        comment_dto = comment_mapper.comment_to_dto(created_comment)
        return comment_dto.model_dump(), 200
    
    @comment_bp.delete('/<int:photo_id>/comment/<int:comment_id>', responses={204: None}, tags=[comment_tag])
    @jwt_required
    def delete_comment(path: PhotoCommentPath):
        current_app.logger.info(f"Deleting comment {path.comment_id} for photo {path.photo_id}")
        comment_validator.validate_delete_request(path.photo_id, path.comment_id)
        comment_service.delete(path.comment_id)
        return '', 204
    
    return comment_bp 