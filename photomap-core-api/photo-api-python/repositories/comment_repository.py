from typing import Optional
from extensions import db
from models.comment import Comment
from dto.page import Page


class CommentRepository:

    def __init__(self):
        self.session = db.session
    
    def find_all_by_photo_id(self, photo_id: int, page: int, size: int) -> Page[Comment]:
        query = self.session.query(Comment).filter(
            Comment.photo_id == photo_id
        ).order_by(Comment.created_at.desc())

        # Calculate pagination
        total_count = query.count()
        offset = page * size

        comments = query.offset(offset).limit(size).all()

        return Page.of(
            content=comments,
            page=page,
            size=size,
            total_elements=total_count
        )
    
    def find_by_id_and_photo_id(self, comment_id: int, photo_id: int) -> Optional[Comment]:
        return self.session.query(Comment).filter(
            Comment.id == comment_id,
            Comment.photo_id == photo_id
        ).first()

    
    def save(self, comment: Comment) -> Comment:
        try:
            self.session.add(comment)
            self.session.commit()
            return comment
        except Exception as e:
            self.session.rollback()
            raise Exception(f"Error saving comment: {str(e)}")
    
    def delete_by_id(self, comment_id: int) -> None:
        try:
            comment = self.session.query(Comment).filter(Comment.id == comment_id).first()
            if comment:
                self.session.delete(comment)
                self.session.commit()
        except Exception as e:
            self.session.rollback()
            raise Exception(f"Error deleting comment: {str(e)}")
    
    def delete_all_by_photo_id(self, photo_id: int) -> None:
        try:
            self.session.query(Comment).filter(Comment.photo_id == photo_id).delete()
            self.session.commit()
        except Exception as e:
            self.session.rollback()
            raise Exception(f"Error deleting comments by photo ID: {str(e)}") 