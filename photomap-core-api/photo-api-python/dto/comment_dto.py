from pydantic import BaseModel, Field
from typing import Optional, List
from datetime import datetime


class CommentDTO(BaseModel):
    id: Optional[int] = Field(default=None, description='Comment ID')
    text: Optional[str] = Field(default=None, description='Comment text')
    userId: Optional[int] = Field(default=None, description='User ID')
    userName: Optional[str] = Field(default=None, description='User name')  
    photoId: Optional[int] = Field(default=None, description='Photo ID')
    createdAt: Optional[datetime] = Field(default=None, description='Creation timestamp')

class CreateCommentDTO(BaseModel):
    text: str = Field(description='Comment text', min_length=1, max_length=1000)

# Path parameter DTOs
class PhotoIdPath(BaseModel):
    photo_id: int = Field(description='Photo ID')


class PhotoCommentPath(BaseModel):
    photo_id: int = Field(description='Photo ID')
    comment_id: int = Field(description='Comment ID')