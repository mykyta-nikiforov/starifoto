from typing import List

from constants.image_file_type import ImageFileType
from extensions import db
from models.image_file import ImageFile
from models.associations import photo_image_files
from dto.photo_dto import PhotoThumbnailDTO
import logging

logger = logging.getLogger(__name__)

class ImageFileRepository:
    def find_all_by_photo_id(self, photo_id: int) -> List[int]:
        try:
            result = db.session.query(ImageFile.id).join(
                photo_image_files, ImageFile.id == photo_image_files.c.image_file_id
            ).filter(photo_image_files.c.photo_id == photo_id).all()
            return [row.id for row in result]
        except Exception as e:
            logger.error(f"Error finding image files by photo ID {photo_id}: {str(e)}")
            raise
    
    def find_photo_id_and_thumbnail_url_by_photo_ids(self, photo_ids: List[int]) -> List[PhotoThumbnailDTO]:
        try:
            results = db.session.query(
                photo_image_files.c.photo_id,
                ImageFile.url
            ).join(
                ImageFile, ImageFile.id == photo_image_files.c.image_file_id
            ).filter(
                photo_image_files.c.photo_id.in_(photo_ids),
                ImageFile.file_type == ImageFileType.THUMBNAIL.value
            ).all()
            thumbnails = []
            for photo_id, thumbnail_url in results:
                thumbnail_dto = PhotoThumbnailDTO(
                    photo_id=photo_id,
                    icon_thumb_url=thumbnail_url
                )
                thumbnails.append(thumbnail_dto)
            
            return thumbnails
            
        except Exception as e:
            logger.error(f"Error finding thumbnails by photo IDs {photo_ids}: {str(e)}")
            raise
    
    def save(self, image_file: ImageFile) -> ImageFile:
        try:
            db.session.add(image_file)
            db.session.commit()
            return image_file
            
        except Exception as e:
            db.session.rollback()
            logger.error(f"Error saving image file: {str(e)}")
            raise
    
    def find_by_id(self, image_file_id: int) -> ImageFile:
        try:
            return db.session.query(ImageFile).filter(ImageFile.id == image_file_id).first()
            
        except Exception as e:
            logger.error(f"Error finding image file by ID {image_file_id}: {str(e)}")
            raise
    
    def delete(self, image_file: ImageFile):
        try:
            db.session.delete(image_file)
            db.session.commit()
            
        except Exception as e:
            db.session.rollback()
            logger.error(f"Error deleting image file: {str(e)}")
            raise 