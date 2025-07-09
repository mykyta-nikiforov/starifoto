import logging
from typing import List, Optional

import imagehash
from PIL import Image
from werkzeug.datastructures import FileStorage

from dto.photo_metadata_dto import PhotoMetadataDTO
from dto.photo_update_metadata_dto import PhotoUpdateMetadataDTO
from exceptions.common_exceptions import ValidationException
from exceptions.photo_exceptions import SimilarPhotosExistException
from mappers.photo_mapper import PhotoMapper
from middleware.auth import get_current_user, is_moderator_or_admin
from repositories.photo_repository import PhotoRepository
from validators.common_photo_validator import CommonPhotoValidator

logger = logging.getLogger(__name__)


class PhotoValidator:
    GET_ICONS_REQUEST_PHOTO_IDS_LIMIT = 500
    
    def __init__(self, photo_mapper: PhotoMapper, photo_repository: PhotoRepository, common_validator: CommonPhotoValidator):
        self.common_validator = common_validator
        self.photo_mapper = photo_mapper
        self.photo_repository = photo_repository

    def validate_on_duplicates(self, files: List[FileStorage]):
        if not files:
            raise ValidationException("File is required")
        file = files[0]
        try:
            file.seek(0)  # Reset file position
            file_bytes = file.read()
            file.seek(0)  # Reset for potential future reads

            # Generate perceptual hash using imagehash
            with Image.open(file) as img:
                p_hash = str(imagehash.phash(img))

            # Reset file position again
            file.seek(0)
            similar_photo_ids = self.photo_repository.find_similar_photos_by_hash(p_hash)
            if similar_photo_ids:
                similar_photos = self.photo_repository.find_all_by_ids_with_relations(similar_photo_ids)
                photo_dtos = []
                for photo in similar_photos:
                    photo_dto = self.photo_mapper.photo_to_details_dto(photo)
                    photo_dtos.append(photo_dto)
                raise SimilarPhotosExistException(
                    "File with the same content already exists",
                    photo_dtos
                )
        except SimilarPhotosExistException:
            raise
        except Exception as e:
            logger.error(f"Failed to validate file: {str(e)}")
            raise ValidationException(f"Failed to validate file: {str(e)}")

    def validate_create_request(self, metadata: PhotoMetadataDTO, files: List[FileStorage]):
            self.common_validator.validate_metadata(metadata)
            if not files or len(files) == 0:
                raise ValidationException("File is required")
            for file in files:
                self.common_validator.validate_file(file)
                
    def validate_update_request(self, photo_id: int, metadata: PhotoUpdateMetadataDTO, files: Optional[List[FileStorage]]):
            self._validate_access(photo_id)
            self.common_validator.validate_metadata(metadata)
            if files:
                for file in files:
                    self.common_validator.validate_file(file)
    
    def validate_delete_request(self, photo_id: int):
            self._validate_access(photo_id)
    
    def validate_get_photo_icons_request(self, photo_ids: List[int]):
        if len(photo_ids) > self.GET_ICONS_REQUEST_PHOTO_IDS_LIMIT:
            raise ValidationException(f"The number of photo IDs cannot exceed {self.GET_ICONS_REQUEST_PHOTO_IDS_LIMIT}")
    
    def _validate_access(self, photo_id: int):
        if photo_id is None:
            raise ValidationException("Photo id is required")
        
        current_user = get_current_user()

        if not is_moderator_or_admin():
            photo_exists = self.photo_repository.exists_by_id_and_user_id(photo_id, current_user['id'])
            
            if not photo_exists:
                raise ValidationException(f"You are not allowed to operate with photo with id {photo_id}")
    

