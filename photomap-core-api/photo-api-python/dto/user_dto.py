from pydantic import BaseModel, Field


class UserBasicDTO(BaseModel):
    id: int = Field(description='User ID')
    name: str = Field(description='User name')


class GetUsersByIdsRequest(BaseModel):
    ids: list[int] = Field(description='List of user IDs')


class GetUsersByIdsResponse(BaseModel):
    users: list[UserBasicDTO] = Field(description='List of users') 