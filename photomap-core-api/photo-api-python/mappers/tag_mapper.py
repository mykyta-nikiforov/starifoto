from models.tag import Tag
from dto.tag_dto import TagDTO

class TagMapper:
    def tag_to_tag_dto(self, tag: Tag) -> TagDTO:
        return TagDTO(name=tag.name) 