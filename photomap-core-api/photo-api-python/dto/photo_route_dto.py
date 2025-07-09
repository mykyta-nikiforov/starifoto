from pydantic import BaseModel, Field
from typing import List, Optional
from dto.photo_dto import PhotoThumbnailDTO
from dto.common_query_params import PaginationQueryParams

class PhotoIdPath(BaseModel):
    photo_id: int = Field(description='Photo ID')

class PhotoFilterQueryParams(PaginationQueryParams):
    tags: Optional[List[str]] = Field(default=None, description='Tags to filter by')
    yearRange: Optional[str] = Field(default=None, description='Year range to filter by [start,end]')

class PhotoThumbnailRequest(BaseModel):
    photo_ids: List[int] = Field(description='List of photo IDs')
    
    def to_dict(self):
        return {'photo_ids': self.photo_ids}

class PhotoThumbnailListResponse(BaseModel):
    thumbnails: List[PhotoThumbnailDTO] = Field(description='List of photo thumbnails')
    
    @classmethod
    def from_thumbnails(cls, thumbnails: List[PhotoThumbnailDTO]):
        return cls(thumbnails=thumbnails) 