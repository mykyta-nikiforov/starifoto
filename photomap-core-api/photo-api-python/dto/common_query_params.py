from pydantic import BaseModel, Field
 
class PaginationQueryParams(BaseModel):
    page: int = Field(default=0, description='Page number (0-based)')
    size: int = Field(default=20, description='Page size') 