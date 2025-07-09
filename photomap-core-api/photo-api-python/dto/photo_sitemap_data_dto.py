from pydantic import BaseModel, Field
 
class PhotoSitemapDataDTO(BaseModel):
    id: int = Field(description='Photo ID')
    updatedAt: str = Field(description='Last update timestamp in ISO format')
    url: str = Field(description='Original file URL') 