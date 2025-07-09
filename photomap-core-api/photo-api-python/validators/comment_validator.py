from repositories.comment_repository import CommentRepository
from middleware.auth import get_current_user, is_moderator_or_admin
from exceptions.common_exceptions import ValidationException, ResourceNotFoundException
import logging

logger = logging.getLogger(__name__)


class CommentValidator:

    def __init__(self, comment_repository: CommentRepository):
        self.comment_repository = comment_repository
    
    def validate_delete_request(self, photo_id: int, comment_id: int) -> None:
        comment = self.comment_repository.find_by_id_and_photo_id(comment_id, photo_id)
        
        if not comment:
            raise ResourceNotFoundException("Comment not found")
        
        current_user = get_current_user()
        
        if not is_moderator_or_admin() and comment.user_id != current_user['id']:
            raise ValidationException("You can delete only your own comments") 