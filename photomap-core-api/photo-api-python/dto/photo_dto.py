from pydantic import BaseModel, Field
from typing import List, Optional

class YearRangeDTO(BaseModel):
    start: int = Field(description='Start year')
    end: int = Field(description='End year')

class CoordinatesDTO(BaseModel):
    latitude: float = Field(description='Latitude coordinate')
    longitude: float = Field(description='Longitude coordinate')
    isApproximate: bool = Field(description='Whether the location is approximate')

class PhotoDTO(BaseModel):
    id: int = Field(description='Photo ID')
    title: str = Field(description='Photo title')
    description: str = Field(description='Photo description')
    source: str = Field(description='Photo source')
    author: str = Field(description='Photo author')
    yearRange: YearRangeDTO = Field(description='Year range when the photo was taken')
    licenseId: int = Field(description='License ID')
    licenseName: str = Field(description='License name')
    tags: List[str] = Field(description='Photo tags')
    url: Optional[str] = Field(description='Original photo URL')
    colorizedUrl: Optional[str] = Field(description='Colorized photo URL')
    userId: int = Field(description='User ID who uploaded the photo')
    createdAt: str = Field(description='Creation timestamp')
    updatedAt: str = Field(description='Last update timestamp')
    coordinates: CoordinatesDTO = Field(description='Photo coordinates')

class PhotoDetailsDTO(PhotoDTO):
    userName: str = Field(description='Name of the user who uploaded the photo')

class PhotoThumbnailDTO(BaseModel):
    photoId: int = Field(description='Photo ID')
    iconThumbUrl: str = Field(description='Thumbnail URL') 