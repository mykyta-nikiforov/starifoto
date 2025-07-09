import logging
import requests
from typing import Optional, List
from exceptions.common_exceptions import InternalException
from dto.user_dto import UserBasicDTO, GetUsersByIdsRequest, GetUsersByIdsResponse

logger = logging.getLogger(__name__)

class UserApiService:

    def __init__(self, user_api_base_url: str, timeout: int):
        self.user_api_base_url = user_api_base_url
        self.timeout = timeout
    
    def find_by_id(self, user_id: int) -> Optional[UserBasicDTO]:
        try:
            users = self.find_all_basic_by_ids([user_id])
            return users[0] if users else None
        except Exception as e:
            logger.error(f"Error finding user by ID: {str(e)}")
            return None
    
    def find_all_basic_by_ids(self, user_ids: List[int]) -> List[UserBasicDTO]:
        try:
            request_dto = GetUsersByIdsRequest(ids=user_ids)
            
            endpoint = f"{self.user_api_base_url}/internal/user/basic-info"
            response = requests.post(
                endpoint,
                json=request_dto.model_dump(),
                timeout=self.timeout
            )
            response.raise_for_status()
            response_data = response.json()
            
            if response_data is None:
                raise InternalException("User service did not respond")
            response_dto = GetUsersByIdsResponse(**response_data)
            return response_dto.users
            
        except requests.exceptions.RequestException as e:
            logger.error(f"Error calling user API: {str(e)}")
            return []
        except Exception as e:
            logger.error(f"Error finding users by IDs: {str(e)}")
            return [] 