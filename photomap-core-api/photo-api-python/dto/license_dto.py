from pydantic import BaseModel, Field

class LicenseDTO(BaseModel):
    id: int = Field(description="License ID")
    name: str = Field(description="License name")
    description: str = Field(description="License description")
    detailsUrl: str = Field(description="License details URL")