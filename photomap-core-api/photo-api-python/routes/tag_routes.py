from typing import List
from flask_openapi3 import APIBlueprint, Tag
from services.tag_service import TagService
from dto.tag_dto import TagDTO
from pydantic import BaseModel, Field
import logging

logger = logging.getLogger(__name__)

class TagSearchQuery(BaseModel):
    name: str = Field(description="Tag name to search for")

def create_tag_routes(tag_service: TagService) -> APIBlueprint:

    tag_tag = Tag(name="Tag API", description="API to work with tags")
    tag_bp = APIBlueprint('tag', __name__, url_prefix='/api/photo/tag')
    
    @tag_bp.get('/search', 
                responses={200: {"description": "List of matching tags", "content": {"application/json": {"schema": {"type": "array", "items": TagDTO.model_json_schema()}}}}},
                tags=[tag_tag],
                summary='Search tags by name')
    def search_tags(query: TagSearchQuery) -> List[TagDTO]:
        logger.info(f"Searching tags with name containing: {query.name}")
        tag_dtos = tag_service.find_by_name_contains(query.name)
        return [tag_dto.model_dump() for tag_dto in tag_dtos]
    
    return tag_bp 