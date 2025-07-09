from models.license import License
from dto.license_dto import LicenseDTO

class LicenseMapper:
    def license_to_license_dto(self, license: License) -> LicenseDTO:
        return LicenseDTO(
            id=license.id,
            name=license.name,
            description=license.description,
            detailsUrl=license.details_url
        ) 