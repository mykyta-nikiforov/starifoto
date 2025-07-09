import logging
from typing import List, Set
from models.tag import Tag
from dto.tag_dto import TagDTO
from repositories.tag_repository import TagRepository
from mappers.tag_mapper import TagMapper

logger = logging.getLogger(__name__)

class TagService:

    def __init__(self, tag_repository: TagRepository, tag_mapper: TagMapper):
        self.tag_repository = tag_repository
        self.tag_mapper = tag_mapper
    
    def get_existing_tags_or_create_new(self, tag_names: List[str]) -> Set[Tag]:
        formatted_tag_names = [tag_name.lower() for tag_name in tag_names]
        
        # Find existing tags by their names
        existing_tags = self.tag_repository.find_by_name_in(formatted_tag_names)
        result = set(existing_tags)
        
        # Create new tags for the ones that don't exist
        existing_tag_names = [tag.name for tag in existing_tags]
        
        new_tags = []
        for tag_name in formatted_tag_names:
            if tag_name not in existing_tag_names:
                new_tag = Tag(name=tag_name)
                new_tags.append(new_tag)
        
        # Save all new tags at once
        if new_tags:
            saved_new_tags = self.tag_repository.save_all(new_tags)
            result.update(saved_new_tags)
        
        return result
    
    def find_by_name_contains(self, name: str) -> List[TagDTO]:
        tags = self.tag_repository.find_by_name_contains_ignore_case(name)
        return [self.tag_mapper.tag_to_tag_dto(tag) for tag in tags] 