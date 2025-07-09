from typing import List, Optional
from pydantic import Field
from dto.photo_metadata_dto import PhotoMetadataDTO
from constants.image_file_type import ImageFileType

class PhotoUpdateMetadataDTO(PhotoMetadataDTO):
    changedImageTypes: Optional[List[ImageFileType]] = Field(
        description='List of image file types that have changed'
    ) 