from pydantic import BaseModel, Field
from typing import List, Optional, TypeVar, Generic, Any

T = TypeVar('T')

class GeometryDTO(BaseModel):
    type: str = Field(description="Geometry type (e.g., 'Point')")
    coordinates: List[float] = Field(description="Coordinates [longitude, latitude]")

class PropertiesDTO(BaseModel):
    photoId: int = Field(description="Photo ID")

class DetailedPropertiesDTO(BaseModel):
    photoId: int = Field(description="Photo ID")
    title: str = Field(description="Photo title")
    url: str = Field(description="Photo URL")
    colorizedUrl: Optional[str] = Field(default=None, description="Colorized photo URL")
    width: int = Field(description="Photo width")
    height: int = Field(description="Photo height")

class LeafDTO(BaseModel, Generic[T]):
    geometry: GeometryDTO = Field(description="Geometry information")
    type: str = Field(description="Feature type")
    properties: T = Field(description="Feature properties")

class ClusterLeavesResponse(BaseModel, Generic[T]):
    leaves: List[LeafDTO[T]] = Field(description="List of cluster leaves")