import logging

from dto.photo_metadata_dto import PhotoMetadataDTO
from exceptions.common_exceptions import ResourceNotFoundException
from models.photo import Photo
from services.license_service import LicenseService
from services.tag_service import TagService

logger = logging.getLogger(__name__)

class PhotoMetadataMapper:
    def __init__(self, license_service: LicenseService, tag_service: TagService):
        self.tag_service = tag_service
        self.license_service = license_service
    
    def set_photo_details(self, photo: Photo, metadata: PhotoMetadataDTO):
        if self._are_different(photo.title, metadata.title):
            photo.title = metadata.title

        if self._are_different(photo.description, metadata.description):
            photo.description = metadata.description

        if self._are_different(photo.author, metadata.author):
            photo.author = metadata.author

        if self._are_different(photo.source, metadata.source):
            photo.source = metadata.source

        metadata_tags = metadata.tags
        if photo.tags is None or self._are_different(set([tag.name for tag in photo.tags]), set(metadata_tags)):
            tags = self.tag_service.get_existing_tags_or_create_new(metadata_tags)
            photo.tags = list(tags)
        
        year_range = metadata.yearRange
        if year_range:
            if (photo.year_start is None or photo.year_end is None or
                self._are_different(photo.year_start, year_range.start) or
                self._are_different(photo.year_end, year_range.end)):
                photo.year_start = year_range.start
                photo.year_end = year_range.end
        
        coordinates = metadata.coordinates
        if coordinates:
            if (photo.latitude is None or photo.longitude is None or photo.is_approximate_location is None or
                self._are_different(photo.latitude, coordinates.latitude) or
                self._are_different(photo.longitude, coordinates.longitude) or
                self._are_different(photo.is_approximate_location, coordinates.isApproximate)):
                photo.latitude = coordinates.latitude
                photo.longitude = coordinates.longitude
                photo.is_approximate_location = coordinates.isApproximate
        
        license_id = metadata.licenseId
        if license_id and (photo.license_id is None or self._are_different(photo.license_id, license_id)):
            license = self.license_service.find_by_id(license_id)
            if license:
                photo.license = license
                photo.license_id = license_id
            else:
                raise ResourceNotFoundException(f"License with id {license_id} not found")
    
    def _are_different(self, current_value, new_value) -> bool:
        if current_value is None and new_value is None:
            return False
        if current_value is None or new_value is None:
            return True
        if isinstance(current_value, set) and isinstance(new_value, set):
            return current_value != new_value
        return current_value != new_value 