from pydantic import BaseModel, Field
from typing import List, Optional

class ClusterIdPath(BaseModel):
    cluster_id: int = Field(description="Cluster ID")

class ClusterLeavesQuery(BaseModel):
    page: int = Field(description="Page number")
    size: int = Field(description="Page size")
    bbox: str = Field(description="Bounding box of a map")
    tags: Optional[List[str]] = Field(default=None, description="Tags to filter by")
    yearRange: Optional[List[int]] = Field(default=None, description="Year range to filter by [start, end]") 