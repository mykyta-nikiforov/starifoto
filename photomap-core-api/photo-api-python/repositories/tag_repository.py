from typing import List, Optional
from models.tag import Tag
from extensions import db
import logging

logger = logging.getLogger(__name__)

class TagRepository:
    
    def find_by_name_contains_ignore_case(self, name: str) -> List[Tag]:
        with db.session.no_autoflush:
            return db.session.query(Tag).filter(Tag.name.ilike(f'%{name}%')).all()
    
    def find_by_name(self, name: str) -> Optional[Tag]:
        with db.session.no_autoflush:
            return db.session.query(Tag).filter(Tag.name == name).first()
    
    def find_by_name_in(self, names: List[str]) -> List[Tag]:
        with db.session.no_autoflush:
            return db.session.query(Tag).filter(Tag.name.in_(names)).all()
    
    def save(self, tag: Tag) -> Tag:
        try:
            db.session.add(tag)
            db.session.flush()
            return tag
        except Exception as e:
            db.session.rollback()
            logger.error(f"Error saving tag '{tag.name}': {str(e)}")
            raise 
    
    def save_all(self, tags: List[Tag]) -> List[Tag]:
        try:
            db.session.add_all(tags)
            db.session.flush()
            return tags
        except Exception as e:
            db.session.rollback()
            logger.error(f"Error saving tags: {str(e)}")
            raise