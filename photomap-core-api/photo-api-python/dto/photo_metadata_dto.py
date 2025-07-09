from pydantic import BaseModel, Field
from typing import List, Optional
from dto.photo_dto import YearRangeDTO, CoordinatesDTO

class PhotoMetadataDTO(BaseModel):
    title: str = Field(description='Photo title')
    description: str = Field(description='Photo description')
    author: Optional[str] = Field(description='Photo author')
    source: str = Field(description='Photo source')
    yearRange: YearRangeDTO = Field(description='Year range when the photo was taken')
    licenseId: int = Field(description='License ID')
    tags: List[str] = Field(description='Photo tags')
    coordinates: CoordinatesDTO = Field(description='Photo coordinates') 