from typing import List, Dict
from models.comment import Comment
from dto.comment_dto import CommentDTO
from dto.page import Page
from services.user_api_service import UserApiService
from dto.user_dto import UserBasicDTO

class CommentMapper:
    def __init__(self, user_api_service: UserApiService):
        self.user_api_service = user_api_service
    
    def _convert_to_dto(self, comment: Comment, user_id_to_name_map: Dict[int, str]) -> CommentDTO:
        comment_dto = CommentDTO()
        comment_dto.id = comment.id
        comment_dto.photoId = comment.photo_id
        comment_dto.userName = user_id_to_name_map.get(comment.user_id, '')
        comment_dto.userId = comment.user_id
        comment_dto.text = comment.text
        comment_dto.createdAt = comment.created_at
        return comment_dto

    def comment_to_dto(self, comment: Comment) -> CommentDTO:
        user_info = self.user_api_service.find_by_id(comment.user_id)
        user_name = user_info.name if user_info else ''
        user_id_to_name_map = {comment.user_id: user_name}
        return self._convert_to_dto(comment, user_id_to_name_map)

    def convert_page_to_dto(self, comments_page: Page[Comment]) -> Page[CommentDTO]:
        user_ids = [comment.user_id for comment in comments_page.content]
        
        users: List[UserBasicDTO] = self.user_api_service.find_all_basic_by_ids(user_ids)
        user_id_to_name_map = {user.id: user.name for user in users}
        
        return comments_page.map(
            lambda comment: self._convert_to_dto(comment, user_id_to_name_map)
        )