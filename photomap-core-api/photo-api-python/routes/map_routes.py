from flask_openapi3 import APIBlueprint, Tag
from dto.map_dto import ClusterIdPath, ClusterLeavesQuery
from dto.supercluster_dto import ClusterLeavesResponse, DetailedPropertiesDTO
from dto.error_dto import GenericErrorResponse
from services.map_data_service import MapDataService
import logging

logger = logging.getLogger(__name__)

def create_map_routes(map_data_service: MapDataService) -> APIBlueprint:
    map_tag = Tag(name="Map API", description="API used by map")
    map_bp = APIBlueprint('map', __name__, url_prefix='/api/photo/map')

    @map_bp.get('/cluster/<int:cluster_id>/leaves',
                tags=[map_tag],
                summary="Get leaves of a cluster",
                description="""Get leaves of a cluster. This is a proxy endpoint to Node.js supercluster backend with additional data.
                Retrieves detailed photo information for cluster leaves including photo metadata, URLs, and dimensions.
                """,
                responses={
                    200: ClusterLeavesResponse[DetailedPropertiesDTO],
                    400: GenericErrorResponse,
                    500: GenericErrorResponse
                })
    def get_cluster_leaves(path: ClusterIdPath, query: ClusterLeavesQuery):
        logger.info(f"Getting cluster leaves for cluster {path.cluster_id}, page={query.page}, size={query.size}, bbox={query.bbox}")
        leaves = map_data_service.get_cluster_leaves(
            path.cluster_id,
            query.page,
            query.size,
            query.bbox,
            query.tags,
            query.yearRange
        )
        response = ClusterLeavesResponse[DetailedPropertiesDTO](leaves=leaves)
        return response.model_dump()
    
    return map_bp