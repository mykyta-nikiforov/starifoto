from werkzeug.datastructures import FileStorage
from services.license_service import LicenseService
from dto.photo_metadata_dto import PhotoMetadataDTO
from exceptions.common_exceptions import ValidationException
from config import Config
import logging

logger = logging.getLogger(__name__)

class CommonPhotoValidator:

    ALLOWED_CONTENT_TYPES = ['image/jpeg']
    
    def __init__(self, license_service: LicenseService):
        self.license_service = license_service
    
    def validate_metadata(self, metadata: PhotoMetadataDTO):
        if not metadata:
            raise ValidationException("Metadata is required")

        if metadata.licenseId is None:
            raise ValidationException("License is required")
        
        if not self.license_service.find_by_id(metadata.licenseId):
            raise ValidationException(f"License with id {metadata.licenseId} does not exist")

        if metadata.yearRange is None or metadata.yearRange.start is None or metadata.yearRange.end is None:
            raise ValidationException("Year range is required")

        if metadata.tags is None or len(metadata.tags) == 0:
            raise ValidationException("Tags cannot be empty")
        
        for tag in metadata.tags:
            if not tag or tag.strip() == '':
                raise ValidationException("Tags cannot be empty")

        if metadata.coordinates is None:
            raise ValidationException("Coordinates are required")
        
        if metadata.coordinates.latitude is None or metadata.coordinates.longitude is None:
            raise ValidationException("Coordinates are required")
        
        if metadata.coordinates.isApproximate is None:
            raise ValidationException("Coordinates approximation is required")
    
    def validate_file(self, file: FileStorage):
        if not file or not file.filename:
            raise ValidationException("File is empty.")

        if file.content_type not in self.ALLOWED_CONTENT_TYPES:
            raise ValidationException("Invalid file type. Only images are allowed.")

        file.seek(0, 2)  # Seek to end
        file_size = file.tell()
        file.seek(0)  # Reset position
        
        max_size_bytes = Config.MAX_FILE_SIZE_MB * 1024 * 1024
        if file_size > max_size_bytes:
            raise ValidationException(f"File size exceeds the allowed limit of {Config.MAX_FILE_SIZE_MB}MB") 