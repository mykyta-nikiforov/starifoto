import logging

from flask_openapi3 import APIBlueprint, Tag

from dto.common_query_params import PaginationQueryParams
from dto.error_dto import GenericErrorResponse
from dto.page import Page
from dto.photo_geojson_data_dto import PhotoGeoJsonDataDTO
from middleware.auth import require_admin
from services.map_data_service import MapDataService
from services.photo_service import PhotoService

logger = logging.getLogger(__name__)


def create_geojson_routes(photo_service: PhotoService, map_data_service: MapDataService) -> APIBlueprint:
    geojson_tag = Tag(name="GeoJSON API", description="API used by geojson-generator")
    geojson_bp = APIBlueprint('geojson', __name__, url_prefix='/api/photo/geojson')

    @geojson_bp.get('/all',
                    tags=[geojson_tag],
                    summary="Get photos data needed to store GeoJSON in MongoDB",
                    description="""Get photos data needed to store GeoJSON in MongoDB. Used by geojson-generator.
                    Retrieves paginated photo data for GeoJSON collection generation including coordinates, metadata, and photo details.
                    """,
                    responses={
                        200: Page[PhotoGeoJsonDataDTO],
                        400: GenericErrorResponse,
                        500: GenericErrorResponse
                    })
    def get_geojson_data(query: PaginationQueryParams):
        logger.info(f"Getting GeoJSON data for page={query.page}, size={query.size}")
        geojson_data = photo_service.get_geojson_data(query.page, query.size)
        return geojson_data.model_dump()

    @geojson_bp.post('/regenerate',
                     tags=[geojson_tag],
                     summary="Regenerate GeoJSON collection for all photos",
                     description="""Regenerate GeoJSON collection for all photos. Admin access required. 
                     Triggers complete regeneration of the GeoJSON data collection in MongoDB.""",
                     responses={
                         204: None,
                         401: GenericErrorResponse,
                         403: GenericErrorResponse,
                         500: GenericErrorResponse
                     })
    @require_admin
    def regenerate_geojson():
        logger.info("Triggering GeoJSON data regeneration")
        map_data_service.regenerate_geojson_data()
        return '', 204

    return geojson_bp
