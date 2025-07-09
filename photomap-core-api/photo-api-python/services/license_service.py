from typing import Optional, List
from models.license import License
from dto.license_dto import LicenseDTO
from repositories.license_repository import LicenseRepository
from mappers.license_mapper import LicenseMapper
from exceptions.common_exceptions import ResourceNotFoundException
import logging

logger = logging.getLogger(__name__)

class LicenseService:    
    def __init__(self, license_repository: LicenseRepository, license_mapper: LicenseMapper):
        self.license_repository = license_repository
        self.license_mapper = license_mapper
    
    def get_active_licenses(self) -> List[LicenseDTO]:
        licenses = self.license_repository.find_all_by_active_true()
        return [self.license_mapper.license_to_license_dto(license) for license in licenses]
    
    def find_by_id(self, license_id: int) -> Optional[License]:
        return self.license_repository.find_by_id(license_id)
    
    def get_by_id(self, license_id: int) -> License:
        license = self.find_by_id(license_id)
        if license is None:
            raise ResourceNotFoundException(f"License with id {license_id} not found")
        return license 