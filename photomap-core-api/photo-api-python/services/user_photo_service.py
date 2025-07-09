import logging
from dto.photo_dto import PhotoDTO
from dto.page import Page
from mappers.photo_mapper import PhotoMapper
from services.photo_service import PhotoService

logger = logging.getLogger(__name__)

class UserPhotoService:

    def __init__(self, photo_service: PhotoService, photo_mapper: PhotoMapper):
        self.photo_service = photo_service
        self.photo_mapper = photo_mapper
    
    def get_photos_by_user_id(self, user_id: int, page: int, size: int) -> Page[PhotoDTO]:
        photos_page = self.photo_service.get_all_by_user_id(user_id, page, size)
        return photos_page.map(self.photo_mapper.photo_to_dto)