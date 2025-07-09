import logging
import requests
from typing import List, Optional
from dto.supercluster_dto import LeafDTO, PropertiesDTO, GeometryDTO

logger = logging.getLogger(__name__)

class SuperClusterApiService:
    def __init__(self, supercluster_api_base_url: str, timeout: int):
        self.supercluster_api_base_url = supercluster_api_base_url
        self.timeout = timeout
    
    def get_cluster_leaves(self, cluster_id: int, page: int, size: int, bbox: str, 
                          tags: Optional[List[str]] = None, 
                          year_range: Optional[List[int]] = None) -> List[LeafDTO[PropertiesDTO]]:
        try:
            url = self._build_cluster_leaves_url(cluster_id, page, size, bbox, tags, year_range)
            response_data = self._make_api_call(url)
            return self._convert_to_leaf_dtos(response_data)
            
        except requests.RequestException as e:
            logger.error(f"Error calling supercluster API: {str(e)}")
            raise RuntimeError(f"Supercluster API did not respond: {str(e)}")
        except Exception as e:
            logger.error(f"Error getting cluster leaves: {str(e)}")
            raise
    
    def _make_api_call(self, url: str) -> dict:
        response = requests.get(url, timeout=self.timeout)
        response.raise_for_status()
        return response.json()
    
    def _convert_to_leaf_dtos(self, response_data: dict) -> List[LeafDTO[PropertiesDTO]]:
        leaves = []
        for leaf_data in response_data.get('leaves', []):
            try:
                geometry_data = leaf_data.get('geometry')
                properties_data = leaf_data.get('properties')
                
                if not geometry_data or not properties_data:
                    continue
                
                geometry = GeometryDTO(
                    type=geometry_data['type'],
                    coordinates=geometry_data['coordinates']
                )
                properties = PropertiesDTO(
                    photoId=properties_data['photoId']
                )
                leaf = LeafDTO(
                    geometry=geometry,
                    type=leaf_data['type'],
                    properties=properties
                )
                leaves.append(leaf)
            except (KeyError, TypeError) as e:
                logger.warning(f"Skipping invalid leaf data: {e}")
                continue
        
        return leaves
    
    def _build_cluster_leaves_url(self, cluster_id: int, page: int, size: int, bbox: str,
                                 tags: Optional[List[str]] = None, 
                                 year_range: Optional[List[int]] = None) -> str:
        url = f"{self.supercluster_api_base_url}/cluster/{cluster_id}/leaves"
        url += f"?page={page}&size={size}&bbox={bbox}"
        
        if tags:
            url += f"&tags={','.join(tags)}"
            
        if year_range and len(year_range) == 2:
            url += f"&yearRange={year_range[0]},{year_range[1]}"
            
        return url
    
 