from typing import Optional, List
from models.photo import Photo
from dto.photo_dto import PhotoDetailsDTO, PhotoDTO, YearRangeDTO, CoordinatesDTO
from dto.photo_geojson_data_dto import PhotoGeoJsonDataDTO
from services.user_api_service import UserApiService
from constants.image_file_type import ImageFileType


class PhotoMapper:

    def __init__(self, user_api_service: UserApiService):
        self.user_api_service = user_api_service

    def photo_to_details_dto(self, photo: Photo) -> PhotoDetailsDTO:
        tags = [tag.name for tag in photo.tags] if photo.tags else []
        tags.sort()
        url = self._get_file_url_by_type(photo.files, ImageFileType.ORIGINAL)
        colorized_url = self._get_file_url_by_type(photo.files, ImageFileType.COLORIZED)
        license_name = photo.license.name if photo.license else ''
        user_info = self.user_api_service.find_by_id(photo.user_id)
        user_name = user_info.name if user_info else ''
        year_range = YearRangeDTO(
            start=photo.year_start,
            end=photo.year_end
        )
        coordinates = CoordinatesDTO(
            latitude=photo.latitude,
            longitude=photo.longitude,
            isApproximate=photo.is_approximate_location
        )
        created_at = photo.created_at.isoformat() if photo.created_at else None
        updated_at = photo.updated_at.isoformat() if photo.updated_at else None
        return PhotoDetailsDTO(
            id=photo.id,
            title=photo.title,
            description=photo.description,
            source=photo.source,
            author=photo.author,
            yearRange=year_range,
            licenseId=photo.license_id,
            licenseName=license_name,
            tags=tags,
            url=url,
            colorizedUrl=colorized_url,
            userId=photo.user_id,
            createdAt=created_at,
            updatedAt=updated_at,
            coordinates=coordinates,
            userName=user_name
        )

    def photo_to_dto(self, photo: Photo) -> PhotoDTO:
        tags = [tag.name for tag in photo.tags] if photo.tags else []
        tags.sort()
        url = self._get_file_url_by_type(photo.files, ImageFileType.ORIGINAL)
        colorized_url = self._get_file_url_by_type(photo.files, ImageFileType.COLORIZED)
        license_name = photo.license.name if photo.license else ''
        year_range = YearRangeDTO(
            start=photo.year_start,
            end=photo.year_end
        )
        coordinates = CoordinatesDTO(
            latitude=photo.latitude,
            longitude=photo.longitude,
            isApproximate=photo.is_approximate_location
        )
        created_at = photo.created_at.isoformat() if photo.created_at else None
        updated_at = photo.updated_at.isoformat() if photo.updated_at else None
        return PhotoDTO(
            id=photo.id,
            title=photo.title,
            description=photo.description,
            source=photo.source,
            author=photo.author,
            yearRange=year_range,
            licenseId=photo.license_id,
            licenseName=license_name,
            tags=tags,
            url=url,
            colorizedUrl=colorized_url,
            userId=photo.user_id,
            createdAt=created_at,
            updatedAt=updated_at,
            coordinates=coordinates
        )

    def photo_to_geojson_data_dto(self, photo: Photo) -> PhotoGeoJsonDataDTO:
        tags = [tag.name for tag in photo.tags] if photo.tags else []
        coordinates = CoordinatesDTO(
            latitude=photo.latitude,
            longitude=photo.longitude,
            isApproximate=photo.is_approximate_location
        )
        year_range = YearRangeDTO(
            start=photo.year_start,
            end=photo.year_end
        )
        icon_thumb_url = self._get_icon_thumb_url(photo.files)

        return PhotoGeoJsonDataDTO(
            coordinates=coordinates,
            photoId=photo.id,
            iconThumbUrl=icon_thumb_url,
            tags=tags,
            yearRange=year_range
        )

    def _get_file_url_by_type(self, files, file_type: ImageFileType) -> Optional[str]:
        if not files:
            return None
        for file in files:
            if file.file_type == file_type.value:
                return file.url
        return None

    def _get_icon_thumb_url(self, files) -> Optional[str]:
        if not files:
            return None

        # Look for THUMBNAIL file type first
        for file in files:
            if file.file_type == ImageFileType.THUMBNAIL.value:
                return file.url

        # Fall back to ORIGINAL if no thumbnail
        for file in files:
            if file.file_type == ImageFileType.ORIGINAL.value:
                return file.url

        return None
