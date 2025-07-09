from typing import List
from flask_openapi3 import APIBlueprint, Tag
from services.license_service import LicenseService
from dto.license_dto import LicenseDTO
import logging

logger = logging.getLogger(__name__)

def create_license_routes(license_service: LicenseService) -> APIBlueprint:
    license_tag = Tag(name="License API", description="API to work with licenses")
    license_bp = APIBlueprint('license', __name__, url_prefix='/api/photo/license')
    
    @license_bp.get('/active', 
                    responses={200: {"description": "List of active licenses", "content": {"application/json": {"schema": {"type": "array", "items": LicenseDTO.model_json_schema()}}}}},
                    tags=[license_tag],
                    summary='Get active licenses')
    def get_active_licenses() -> List[LicenseDTO]:
        licenses = license_service.get_active_licenses()
        return [license.model_dump() for license in licenses]
    
    return license_bp 