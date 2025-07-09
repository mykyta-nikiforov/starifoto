import logging
import requests
from typing import List, Optional, Dict, TYPE_CHECKING
from dto.supercluster_dto import LeafDTO, DetailedPropertiesDTO, GeometryDTO
from constants.image_file_type import ImageFileType

if TYPE_CHECKING:
    from services.photo_service import PhotoService
    from services.supercluster_api_service import SuperClusterApiService

logger = logging.getLogger(__name__)

class MapDataService:

    def __init__(self, photo_service: 'PhotoService', supercluster_api_service: 'SuperClusterApiService', 
                 geojson_generator_base_url: str, geojson_generator_timeout: int):
        self.photo_service = photo_service
        self.supercluster_api_service = supercluster_api_service
        self.geojson_generator_base_url = geojson_generator_base_url
        self.geojson_generator_timeout = geojson_generator_timeout
    
    def get_cluster_leaves(self, cluster_id: int, page: int, size: int, bbox: str,
                          tags: Optional[List[str]] = None, 
                          year_range: Optional[List[int]] = None) -> List[LeafDTO[DetailedPropertiesDTO]]:
        cluster_leaves = self.supercluster_api_service.get_cluster_leaves(
            cluster_id, page, size, bbox, tags, year_range
        )
        
        photo_ids = [leaf.properties.photoId for leaf in cluster_leaves]

        if not photo_ids:
            return []
        
        photo_details_map = self._get_photo_details_map(photo_ids)

        result = []
        for leaf in cluster_leaves:
            photo_details = photo_details_map.get(leaf.properties.photoId)
            if photo_details:
                detailed_leaf = LeafDTO(
                    geometry=leaf.geometry,
                    type=leaf.type,
                    properties=photo_details
                )
                result.append(detailed_leaf)
        
        return result
    
    def regenerate_geojson_data(self):
        url = f"{self.geojson_generator_base_url}/internal/geojson/regenerate"
        response = requests.post(url, timeout=self.geojson_generator_timeout)
        response.raise_for_status()
        
        logger.info("GeoJSON data regeneration triggered successfully")
    
    def _get_photo_details_map(self, photo_ids: List[int]) -> Dict[int, DetailedPropertiesDTO]:
        photos = self.photo_service.photo_repository.find_by_ids_with_files(photo_ids)
        
        details_map = {}
        for photo in photos:
            original_file = None
            colorized_file = None
            
            for file in photo.files:
                if file.file_type == ImageFileType.ORIGINAL.value:
                    original_file = file
                elif file.file_type == ImageFileType.COLORIZED.value:
                    colorized_file = file
            
            if original_file:
                details = DetailedPropertiesDTO(
                    photoId=photo.id,
                    title=photo.title,
                    url=original_file.url,
                    colorizedUrl=colorized_file.url if colorized_file else None,
                    width=original_file.width,
                    height=original_file.height
                )
                details_map[photo.id] = details
        
        return details_map 