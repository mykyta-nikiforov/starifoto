import logging

from dto.page import Page
from exceptions.common_exceptions import ResourceNotFoundException
from middleware.auth import get_current_user
from models.comment import Comment
from repositories.comment_repository import CommentRepository
from repositories.photo_repository import PhotoRepository

logger = logging.getLogger(__name__)


class CommentService:

    def __init__(self, comment_repository: CommentRepository, photo_repository: PhotoRepository):
        self.comment_repository = comment_repository
        self.photo_repository = photo_repository

    def find_all_by_photo_id(self, photo_id: int, page: int, size: int) -> Page[Comment]:
        return self.comment_repository.find_all_by_photo_id(photo_id, page, size)

    def create(self, photo_id: int, text: str) -> Comment:
        photo = self.photo_repository.find_by_id_with_relations(photo_id)
        if not photo:
            raise ResourceNotFoundException("Photo not found")

        comment = self._create_comment(photo.id, text)
        return self.comment_repository.save(comment)

    def delete(self, comment_id: int) -> None:
        self.comment_repository.delete_by_id(comment_id)

    def delete_all_by_photo_id(self, photo_id: int) -> None:
        self.comment_repository.delete_all_by_photo_id(photo_id)

    def _create_comment(self, photo_id: int, text: str) -> Comment:
        comment = Comment()
        comment.photo_id = photo_id
        comment.user_id = get_current_user()['id']
        comment.text = text
        return comment
