import logging
from typing import List, Optional, TYPE_CHECKING

from dto.page import Page

from dto.photo_geojson_data_dto import PhotoGeoJsonDataDTO
from dto.photo_sitemap_data_dto import PhotoSitemapDataDTO
from exceptions.common_exceptions import ResourceNotFoundException
from models.photo import Photo
from repositories.photo_repository import PhotoRepository

if TYPE_CHECKING:
    from repositories.photo_repository import PhotoRepository
    from mappers.photo_mapper import PhotoMapper

logger = logging.getLogger(__name__)

class PhotoService:

    def __init__(self, photo_repository: 'PhotoRepository', photo_mapper: 'PhotoMapper'):
        self.photo_repository = photo_repository
        self.photo_mapper = photo_mapper
    
    def find_by_id(self, photo_id: int) -> Optional[Photo]:
        return self.photo_repository.find_by_id_with_relations(photo_id)
    
    def get_by_id(self, photo_id: int) -> Photo:
        photo = self.photo_repository.find_by_id_with_relations(photo_id)
        if not photo:
            raise ResourceNotFoundException(f"Photo with ID {photo_id} not found")
        return photo
    
    def save(self, photo: Photo) -> Photo:
        return self.photo_repository.save(photo)
    
    def save_all(self, photos: List[Photo]):
        self.photo_repository.save_all(photos)
    
    def delete_by_id(self, photo_id: int):
        self.photo_repository.delete_by_id(photo_id)

    def get_all(self, page: int, size: int) -> Page[Photo]:
        return self.photo_repository.find_all_with_relations_paginated(page, size)
    
    def get_all_by_user_id(self, user_id: int, page: int, size: int) -> Page[Photo]:
        return self.photo_repository.find_all_by_user_id_with_relations_paginated(user_id, page, size)
    
    def get_all_for_sitemap(self, page: int, size: int) -> Page[PhotoSitemapDataDTO]:
        return self.photo_repository.find_all_sitemap_data_page(page, size)

    def get_geojson_data(self, page: int, size: int) -> Page[PhotoGeoJsonDataDTO]:
        photos_page = self.photo_repository.find_all_for_geojson_paginated(page, size)
        if photos_page.numberOfElements == 0:
            return Page.empty(page=page, size=size)
        
        geojson_items = []
        for photo in photos_page.content:
            geojson_dto = self.photo_mapper.photo_to_geojson_data_dto(photo)
            geojson_items.append(geojson_dto)
        
        return Page.of(
            content=geojson_items,
            page=photos_page.page,
            size=photos_page.size,
            total_elements=photos_page.totalElements
        )