import asyncio
import logging
import os
import tempfile

from typing import List, Dict, Optional
from concurrent.futures import ThreadPoolExecutor

from flask import current_app
from werkzeug.datastructures import FileStorage

from constants.image_file_type import ImageFileType
from dto.page import Page
from dto.photo_dto import PhotoDTO, PhotoDetailsDTO, PhotoThumbnailDTO
from dto.photo_metadata_dto import PhotoMetadataDTO
from dto.photo_sitemap_data_dto import PhotoSitemapDataDTO
from dto.photo_update_metadata_dto import PhotoUpdateMetadataDTO
from mappers.photo_mapper import PhotoMapper
from mappers.photo_metadata_mapper import PhotoMetadataMapper
from middleware.auth import get_current_user
from models.photo import Photo
from services.image_file_service import ImageFileService
from services.kafka_service import KafkaService
from services.photo_file_upload_service import PhotoFileUploadService
from services.photo_service import PhotoService
from services.user_api_service import UserApiService
from utils.async_utils import AsyncUtils
from validators.photo_validator import PhotoValidator

logger = logging.getLogger(__name__)


class PhotoManager:
    def __init__(self, 
                 photo_mapper: PhotoMapper, 
                 photo_validator: PhotoValidator, 
                 photo_metadata_mapper: PhotoMetadataMapper,
                 photo_service: PhotoService,
                 photo_file_upload_service: PhotoFileUploadService,
                 image_file_service: ImageFileService,
                 user_api_service: UserApiService,
                 kafka_service: KafkaService,
                 thread_pool: ThreadPoolExecutor):
        self.photo_mapper = photo_mapper
        self.photo_validator = photo_validator
        self.photo_metadata_mapper = photo_metadata_mapper
        self.photo_service = photo_service
        self.photo_file_upload_service = photo_file_upload_service
        self.image_file_service = image_file_service
        self.user_api_service = user_api_service
        self.kafka_service = kafka_service
        self.thread_pool = thread_pool
    
    def get_photo_details(self, photo_id: int) -> PhotoDetailsDTO:
        photo = self.photo_service.get_by_id(photo_id)
        user_info = self.user_api_service.find_by_id(photo.user_id)
        
        photo_dto = self.photo_mapper.photo_to_details_dto(photo)
        photo_dto.userName = user_info.name if user_info else ''
        return photo_dto
    
    async def upload_photo_files_async(self, photo_id: int, temp_files: List[str], flask_app):
        try:
            logger.info(f"Starting async upload for photo {photo_id}")
            
            def upload_in_context():
                with flask_app.app_context():
                    photo = self.photo_service.get_by_id(photo_id)
                    uploaded_photo = self.photo_file_upload_service.upload_new_photo_files(photo, temp_files)
                    logger.info(f"Photo {photo_id} uploaded successfully")
                    return {"status": "success", "photo_id": photo_id}
            
            loop = asyncio.get_event_loop()
            result = await loop.run_in_executor(self.thread_pool, upload_in_context)
            return result
            
        except Exception as e:
            logger.error(f"Error in async upload for photo {photo_id}: {str(e)}")
            await self._cleanup_temp_files_async(temp_files)
            raise
    
    async def update_photo_files_async(self, photo_id: int, temp_files: List[str], changed_image_types: List[ImageFileType],
                                       flask_app):
        try:
            logger.info(f"Starting async file update for photo {photo_id}")
            
            def update_in_context():
                with flask_app.app_context():
                    photo = self.photo_service.get_by_id(photo_id)
                    self.photo_file_upload_service.save_photo_files(photo, temp_files, changed_image_types)
                    logger.info(f"Photo {photo_id} files updated successfully")
                    return {"status": "success", "photo_id": photo_id}
            
            loop = asyncio.get_event_loop()
            result = await loop.run_in_executor(self.thread_pool, update_in_context)
            return result
            
        except Exception as e:
            logger.error(f"Error updating files for photo {photo_id}: {str(e)}")
            await self._cleanup_temp_files_async(temp_files)
            raise
    
    async def _cleanup_temp_files_async(self, temp_files: List[str]):
        try:
            def cleanup():
                for file_path in temp_files:
                    if os.path.exists(file_path):
                        try:
                            os.remove(file_path)
                            logger.info(f"Cleaned up temporary file: {file_path}")
                        except Exception as e:
                            logger.error(f"Failed to clean up {file_path}: {e}")
            
            loop = asyncio.get_event_loop()
            await loop.run_in_executor(self.thread_pool, cleanup)
            
        except Exception as e:
            logger.error(f"Error during cleanup: {str(e)}")
    
    def upload_photo(self, metadata: PhotoMetadataDTO, files: List[FileStorage]) -> PhotoDTO:
        photo = self._build_photo(metadata)
        saved_photo = self.photo_service.save(photo)
        temp_files = self._convert_multipart_files_to_temp_files(files)
        
        flask_app = current_app._get_current_object()
        upload_coro = self.upload_photo_files_async(saved_photo.id, temp_files, flask_app)
        AsyncUtils.run_async_task(upload_coro)
        
        photo_dto = self.photo_mapper.photo_to_dto(saved_photo)
        return photo_dto

    def delete_photo(self, photo_id: int):
        file_ids = self.image_file_service.get_photo_files_ids_by_photo_id(photo_id)
        self.photo_service.delete_by_id(photo_id)
        
        flask_app = current_app._get_current_object()
        delete_coro = self.image_file_service.delete_photo_files_async(
            photo_id, file_ids, flask_app
        )
        AsyncUtils.run_async_task(delete_coro)
    
    def update_photo(self, photo_id: int, metadata: PhotoUpdateMetadataDTO,
                     files: Optional[List[FileStorage]] = None) -> PhotoDTO:
        existing_photo = self.photo_service.get_by_id(photo_id)
        geo_changed = self._geo_json_related_data_changed(existing_photo, metadata)
        self.photo_metadata_mapper.set_photo_details(existing_photo, metadata)
        saved_photo = self.photo_service.save(existing_photo)
        if geo_changed:
            flask_app = current_app._get_current_object()
            kafka_coro = self.kafka_service.send_kafka_message_async('photo_update', saved_photo, flask_app)
            AsyncUtils.run_async_task(kafka_coro)
        
        if metadata.changedImageTypes is not None and len(metadata.changedImageTypes) > 0 and files is not None:
            temp_files = self._convert_multipart_files_to_temp_files(files)
            
            flask_app = current_app._get_current_object()
            changed_image_types = metadata.changedImageTypes
            update_coro = self.update_photo_files_async(photo_id, temp_files, changed_image_types, flask_app)
            AsyncUtils.run_async_task(update_coro)
        
        photo_dto = self.photo_mapper.photo_to_dto(saved_photo)
        return photo_dto

    def get_all(self, page: int, size: int) -> Page[PhotoDetailsDTO]:
        photos_page = self.photo_service.get_all(page, size)
            
        user_ids = [photo.user_id for photo in photos_page.content]
        user_names = self._get_user_names_map(user_ids)
            
        photos_dto = []
        for photo in photos_page.content:
            photo_dto = self.photo_mapper.photo_to_details_dto(photo)
            photo_dto.userName = user_names.get(photo.user_id, '')
            photos_dto.append(photo_dto)

        return Page.of(
            content=photos_dto,
            page=page,
            size=size,
            total_elements=photos_page.totalElements
        )
    
    def get_all_for_sitemap(self, page: int, size: int) -> Page[PhotoSitemapDataDTO]:
        return self.photo_service.get_all_for_sitemap(page, size)
    
    def get_thumbnail_urls_by_photo_ids(self, photo_ids: List[int]) -> List[PhotoThumbnailDTO]:
        return self.image_file_service.get_thumbnail_urls_by_photo_ids(photo_ids)
    
    def _build_photo(self, metadata: PhotoMetadataDTO) -> Photo:
        photo = Photo()
        photo.files = []
        
        current_user = get_current_user()
        if current_user:
            photo.user_id = current_user.get('id')
        else:
            raise ValueError("User not authenticated")
        
        self.photo_metadata_mapper.set_photo_details(photo, metadata)
        return photo
    
    def _convert_multipart_files_to_temp_files(self, files: List[FileStorage]) -> List[str]:
        """Convert multipart files to temporary files and return their paths"""
        temp_files = []
        
        for file in files:
            if file and file.filename:
                # Create temporary file
                temp_dir = tempfile.gettempdir()
                temp_file_path = os.path.join(temp_dir, f"upload_{file.filename}")
                
                # Save file
                file.save(temp_file_path)
                temp_files.append(temp_file_path)
        
        return temp_files
    
    def _get_file_types_from_files(self, files: List[FileStorage]) -> List[ImageFileType]:
        """Get file types based on file order (first is ORIGINAL, rest are COLORIZED)"""
        file_types = []
        for index, file in enumerate(files):
            if index == 0:
                file_types.append(ImageFileType.ORIGINAL)
            else:
                file_types.append(ImageFileType.COLORIZED)
        return file_types
    
    def _geo_json_related_data_changed(self, original: Photo, metadata: PhotoUpdateMetadataDTO) -> bool:
        if (metadata.coordinates.latitude != original.latitude or
                metadata.coordinates.longitude != original.longitude):
            return True
        
        if (metadata.yearRange.start != original.year_start or
                metadata.yearRange.end != original.year_end):
            return True
        
        original_tags = set([tag.name for tag in original.tags]) if original.tags else set()
        new_tags = set(metadata.tags)
        if original_tags != new_tags:
            return True
        
        return False
    
    def _get_user_names_map(self, user_ids: List[int]) -> Dict[int, str]:
        users = self.user_api_service.find_all_basic_by_ids(user_ids)
        user_names = {}
        for user in users:
            user_names[user.id] = user.name
        return user_names 
