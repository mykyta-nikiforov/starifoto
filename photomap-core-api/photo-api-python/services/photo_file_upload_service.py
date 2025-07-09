import logging
from typing import List, TYPE_CHECKING
from models.photo import Photo
from models.image_file import ImageFile
from services.image_file_service import ImageFileService
from services.kafka_service import KafkaService
from websocket.notification_sender import NotificationSender
from dto.user_photo_notification_dto import UserPhotoNotificationDTO
from constants.image_file_type import ImageFileType
from constants.user_photo_notification_type import UserPhotoNotificationType
import os

if TYPE_CHECKING:
    from mappers.photo_mapper import PhotoMapper
    from services.photo_service import PhotoService

logger = logging.getLogger(__name__)

class PhotoFileUploadService:
    def __init__(self, 
                 image_file_service: ImageFileService, 
                 kafka_service: KafkaService, 
                 photo_mapper: 'PhotoMapper',
                 photo_service: 'PhotoService',
                 notification_sender: NotificationSender):
        self.image_file_service = image_file_service
        self.kafka_service = kafka_service
        self.photo_mapper = photo_mapper
        self.photo_service = photo_service
        self.notification_sender = notification_sender
    
    def upload_new_photo_files(self, photo: Photo, files: List[str]) -> Photo:
        logger.info(f"Uploading new photo files for photo with id: {photo.id}")
        
        try:
            updated_photo = self._upload_and_save_files(photo, files)
            
            self._notify_websocket(updated_photo.id, updated_photo.user_id, UserPhotoNotificationType.PHOTO_UPDATED)
            self._notify_kafka_new_photo(updated_photo)
            return updated_photo
            
        finally:
            self._cleanup_temp_files(files)
    
    def _upload_and_save_files(self, photo: Photo, files: List[str]) -> Photo:
        for index, file_path in enumerate(files):
            file_type = self._get_image_file_type_by_index(index)
            self._upload_single_file_to_photo(photo, file_path, file_type)

        return self.photo_service.save(photo)
    
    def save_photo_files(self, photo: Photo, files: List[str], file_types: List[ImageFileType]) -> Photo:
        try:
            updated_files = self._process_file_updates(photo, files, file_types)
            photo.files = updated_files
            updated_photo = self.photo_service.save(photo)

            self._notify_after_save(updated_photo, file_types)
            return updated_photo
            
        finally:
            self._cleanup_temp_files(files)
    
    def _process_file_updates(self, photo: Photo, files: List[str], file_types: List[ImageFileType]) -> List[ImageFile]:
        updated_files = list(photo.files)
        
        for index, file_path in enumerate(files):
            file_type = file_types[index]
            updated_files = self._process_single_file(updated_files, file_path, file_type)
        
        # Handle case where colorized file is removed
        updated_files = self._handle_colorized_file_removal(updated_files, files, file_types)
        
        return updated_files
    

    
    def _upload_single_file_to_photo(self, photo: Photo, file_path: str, file_type: ImageFileType):
        photo_image_file = self.image_file_service.upload_file(file_path, file_type)
        photo.files.append(photo_image_file)
        
        if file_type == ImageFileType.ORIGINAL:
            thumbnail_file = self.image_file_service.generate_thumbnail_by_file_name(photo_image_file.name)
            photo.files.append(thumbnail_file)
    
    def _process_single_file(self, updated_files: List[ImageFile], file_path: str, file_type: ImageFileType) -> List[ImageFile]:
        # Delete old file of same type
        self._delete_old_file(updated_files, file_type)
        
        # Upload new file
        new_file = self.image_file_service.upload_file(file_path, file_type)
        updated_files.append(new_file)
        
        # Handle thumbnail generation for original files
        if file_type == ImageFileType.ORIGINAL:
            updated_files = self._handle_thumbnail_generation(updated_files, new_file)
        
        return updated_files
    
    def _handle_thumbnail_generation(self, updated_files: List[ImageFile], original_file: ImageFile) -> List[ImageFile]:
        self._delete_old_file(updated_files, ImageFileType.THUMBNAIL)
        thumbnail_file = self.image_file_service.generate_thumbnail_by_file_name(original_file.name)
        updated_files.append(thumbnail_file)
        return updated_files
    
    def _handle_colorized_file_removal(self, updated_files: List[ImageFile], files: List[str], file_types: List[ImageFileType]) -> List[ImageFile]:
        if ImageFileType.COLORIZED in file_types and len(files) < len(file_types):
            self._delete_old_file(updated_files, ImageFileType.COLORIZED)
        return updated_files
    
    def _cleanup_temp_files(self, files: List[str]):
        for file_path in files:
            if os.path.exists(file_path):
                os.remove(file_path)
    
    def _notify_after_save(self, photo: Photo, file_types: List[ImageFileType]):
        self._notify_websocket(photo.id, photo.user_id, UserPhotoNotificationType.PHOTO_UPDATED)
        has_original_file_change = ImageFileType.ORIGINAL in file_types
        if has_original_file_change:
            self._notify_kafka_updated_photo(photo)
    
    def _delete_old_file(self, photo_files: List[ImageFile], file_type: ImageFileType):
        old_file = None
        for file in photo_files:
            if file.file_type == file_type.value:
                old_file = file
                break
        
        if old_file:
            photo_files.remove(old_file)
            self.image_file_service.delete(old_file.id)
    
    def _notify_kafka_new_photo(self, photo: Photo):
        photo_dto = self.photo_mapper.photo_to_geojson_data_dto(photo)
        self.kafka_service.push_new_photo_event(photo_dto)

    def _notify_kafka_updated_photo(self, photo: Photo):
        photo_dto = self.photo_mapper.photo_to_geojson_data_dto(photo)
        self.kafka_service.push_update_photo_event(photo_dto)
    
    def _notify_websocket(self, photo_id: int, user_id: int, notification_type: UserPhotoNotificationType):
        message = UserPhotoNotificationDTO(
            photoId=photo_id,
            userId=user_id,
            type=notification_type.value
        )
        self.notification_sender.send_user_photo_notification(message)
    
    def _get_image_file_type_by_index(self, index: int) -> ImageFileType:
        """Get image file type by index - first file is ORIGINAL, rest are COLORIZED"""
        return ImageFileType.ORIGINAL if index == 0 else ImageFileType.COLORIZED 