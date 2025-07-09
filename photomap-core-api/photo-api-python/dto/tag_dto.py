from pydantic import BaseModel, Field

class TagDTO(BaseModel):
    name: str = Field(description="Tag name")