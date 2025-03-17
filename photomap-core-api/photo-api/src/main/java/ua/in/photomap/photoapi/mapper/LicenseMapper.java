package ua.in.photomap.photoapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ua.in.photomap.photoapi.dto.LicenseDTO;
import ua.in.photomap.photoapi.model.License;

@Mapper
public interface LicenseMapper {
    LicenseMapper INSTANCE = Mappers.getMapper(LicenseMapper.class);

    @Mappings(
            {
                    @Mapping(target = "id", source = "id"),
                    @Mapping(target = "name", source = "name"),
                    @Mapping(target = "description", source = "description"),
                    @Mapping(target = "detailsUrl", source = "detailsUrl")
            }
    )
    LicenseDTO licenseToLicenseDto(License entity);
}