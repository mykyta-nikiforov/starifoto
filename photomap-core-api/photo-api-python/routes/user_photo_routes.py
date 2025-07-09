from flask import current_app
from flask_openapi3 import APIBlueprint, Tag
from pydantic import BaseModel, Field

from services.user_photo_service import UserPhotoService
from dto.photo_dto import PhotoDTO
from dto.page import Page
from dto.common_query_params import PaginationQueryParams
import logging

logger = logging.getLogger(__name__)

class UserIdPath(BaseModel):
    user_id: int = Field(description='User ID')

def create_user_photo_routes(user_photo_service: UserPhotoService) -> APIBlueprint:
    user_photo_tag = Tag(name="User Photo API", description="API to work with user photos")
    user_photo_bp = APIBlueprint('user_photo', __name__, url_prefix='/api/photo/user')

    @user_photo_bp.get('/<int:user_id>', responses={200: Page[PhotoDTO]}, tags=[user_photo_tag])
    def get_photos_by_user_id(path: UserIdPath, query: PaginationQueryParams):
        current_app.logger.info(f"Getting photos for user {path.user_id}, page={query.page}, size={query.size}")
        photos_page = user_photo_service.get_photos_by_user_id(path.user_id, query.page, query.size)
        return photos_page.model_dump()
    
    return user_photo_bp 