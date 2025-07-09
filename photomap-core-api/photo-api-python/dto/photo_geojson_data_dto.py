from pydantic import BaseModel, Field
from typing import List, Optional
from dto.photo_dto import CoordinatesDTO, YearRangeDTO

class PhotoGeoJsonDataDTO(BaseModel):
    coordinates: CoordinatesDTO = Field(description="Photo coordinates")
    photoId: int = Field(description="Photo ID")
    iconThumbUrl: Optional[str] = Field(default=None, description="Icon thumbnail URL")
    tags: List[str] = Field(description="Photo tags")
    yearRange: YearRangeDTO = Field(description="Year range") 