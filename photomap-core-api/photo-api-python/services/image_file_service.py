import os
import logging
from PIL import Image
from typing import List, Optional
from models.image_file import ImageFile
from dto.photo_dto import PhotoThumbnailDTO
from repositories.image_file_repository import ImageFileRepository
from services.cloud_storage_service import CloudStorageService
from services.thumbnail_generation_service import ThumbnailGenerationService
from constants.storage_folder import StorageFolder
from constants.thumb_dimensions import ThumbDimensions
from constants.image_file_type import ImageFileType
from exceptions.photo_exceptions import FileUploadInternalException
from exceptions.common_exceptions import ResourceNotFoundException
import imagehash
from io import BytesIO
import asyncio
from concurrent.futures import ThreadPoolExecutor
from services.kafka_service import KafkaService

logger = logging.getLogger(__name__)


class ImageFileService:

    def __init__(self,
                 cloud_storage_service: CloudStorageService,
                 kafka_service: KafkaService,
                 image_file_repository: ImageFileRepository,
                 thumbnail_generation_service: ThumbnailGenerationService,
                 thread_pool: ThreadPoolExecutor):
        self.image_file_repository = image_file_repository
        self.cloud_storage_service = cloud_storage_service
        self.kafka_service = kafka_service
        self.thumbnail_generation_service = thumbnail_generation_service
        self.thread_pool = thread_pool

    def upload_file(self, file_path: str, file_type: ImageFileType) -> ImageFile:
        try:
            logger.info(f"Uploading file to File Storage. File path: {file_path}")

            # Read file and get image info
            with open(file_path, 'rb') as f:
                file_bytes = f.read()

            with Image.open(file_path) as image:
                width = image.width
                height = image.height

            filename = os.path.basename(file_path)

            blob = self.cloud_storage_service.upload_file(file_bytes, filename, StorageFolder.PHOTOS.value)
            blob_url = self._build_link(blob)

            image_hash = self._generate_hash_if_original(file_bytes, file_type)

            image_file = self._handle_uploaded_file_response(blob.name, blob_url, height, width, file_type.value,
                                                             image_hash)
            logger.info(f"File uploaded successfully: {filename}")
            return image_file

        except Exception as e:
            logger.error(f"Error uploading file: {str(e)}")
            raise FileUploadInternalException(f"Error while reading the image file: {str(e)}")

    def generate_thumbnail_by_file_name(self, file_name: str) -> ImageFile:
        logger.info(f"Creating thumbnail for file: {file_name}")

        blob = self.cloud_storage_service.get_file(file_name)
        if not blob:
            raise ResourceNotFoundException(f"File not found in cloud storage: {file_name}")

        image_data = blob.download_as_bytes()
        thumbnail_bytes = self.thumbnail_generation_service.generate(image_data)

        original_filename = os.path.basename(file_name)
        thumbnail_blob = self.cloud_storage_service.upload_file(
            thumbnail_bytes, original_filename, StorageFolder.THUMBNAILS.value
        )

        thumbnail_dimensions = ThumbDimensions.WIDTH_IN_PIXELS.value
        thumbnail_file = self._handle_uploaded_file_response(
            thumbnail_blob.name,
            self._build_link(thumbnail_blob),
            thumbnail_dimensions,
            thumbnail_dimensions,
            ImageFileType.THUMBNAIL.value,
            None
        )

        logger.info(f"Thumbnail created successfully: {original_filename}")
        return thumbnail_file

    def delete(self, image_file_id: int):
        image_file = self.image_file_repository.find_by_id(image_file_id)
        if not image_file:
            raise ResourceNotFoundException("Photo file not found")

        self.image_file_repository.delete(image_file)

        deletion_success = self.cloud_storage_service.delete_file(image_file.name)

        if deletion_success:
            logger.info(f"File deleted from File Storage. File name: {image_file.name}")
        else:
            logger.error(f"Can't delete file from File Storage. File name: {image_file.name}")

    def get_photo_files_ids_by_photo_id(self, photo_id: int) -> List[int]:
        return self.image_file_repository.find_all_by_photo_id(photo_id)

    def get_thumbnail_urls_by_photo_ids(self, photo_ids: List[int]) -> List[PhotoThumbnailDTO]:
        return self.image_file_repository.find_photo_id_and_thumbnail_url_by_photo_ids(photo_ids)

    def _handle_uploaded_file_response(self, file_name: str, file_url: str, height: int, width: int,
                                       file_type: str, p_hash: Optional[str]) -> ImageFile:
        image_file = ImageFile(
            name=file_name,
            url=file_url,
            height=height,
            width=width,
            file_type=file_type,
            image_phash=p_hash
        )
        return self.image_file_repository.save(image_file)

    def _generate_hash_if_original(self, file_bytes: bytes, file_type: ImageFileType) -> Optional[str]:
        """Generate perceptual hash only for original files"""
        if file_type == ImageFileType.ORIGINAL:
            return self._generate_perceptual_hash(file_bytes)
        return None

    def _generate_perceptual_hash(self, image_bytes: bytes) -> str:
        with Image.open(BytesIO(image_bytes)) as img:
            hash_value = imagehash.phash(img)
            return str(hash_value)

    def _build_link(self, blob) -> str:
        return f"https://storage.googleapis.com/{blob.bucket.name}/{blob.name}"

    async def delete_photo_files_async(self, photo_id: int, file_ids: List[int], flask_app):
        try:
            logger.info(f"Starting async deletion for photo {photo_id}")

            def delete_in_context():
                with flask_app.app_context():
                    for file_id in file_ids:
                        self.delete(file_id)

                    self.kafka_service.push_delete_photo_event(photo_id)

                    logger.info(f"Photo {photo_id} deleted successfully (files: {len(file_ids)})")
                    return {"status": "success", "photo_id": photo_id, "files_deleted": len(file_ids)}

            loop = asyncio.get_event_loop()
            result = await loop.run_in_executor(self.thread_pool, delete_in_context)
            return result

        except Exception as e:
            logger.error(f"Error in async delete for photo {photo_id}: {str(e)}")
            raise
