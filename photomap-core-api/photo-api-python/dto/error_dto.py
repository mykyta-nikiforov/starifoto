from pydantic import BaseModel


class GenericErrorResponse(BaseModel):
    timestamp: str
    status: int
    error: str
    message: str
    path: str
