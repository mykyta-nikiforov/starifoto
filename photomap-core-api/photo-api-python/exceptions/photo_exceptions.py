from typing import List, Optional

from dto.photo_dto import PhotoDTO


class SimilarPhotosExistException(Exception):
    def __init__(self, message: str, similar_photos: Optional[List[PhotoDTO]] = None):
        super().__init__(message)
        self.similar_photos = similar_photos or []


class FileUploadInternalException(Exception):
    def __init__(self, message: str):
        super().__init__(message) 