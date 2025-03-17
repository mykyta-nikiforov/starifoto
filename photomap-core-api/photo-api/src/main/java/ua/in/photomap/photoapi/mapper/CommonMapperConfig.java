package ua.in.photomap.photoapi.mapper;

import org.mapstruct.MapperConfig;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Mappings;
import ua.in.photomap.common.photo.model.dto.CommonPhotoDetailsDTO;
import ua.in.photomap.photoapi.model.PhotoDetails;

@MapperConfig
        (uses = CommonMapper.class)
public interface CommonMapperConfig {

    @Mappings(
            {
                    @Mapping(target = "id", source = "id"),
                    @Mapping(target = "title", source = "title"),
                    @Mapping(target = "description", source = "description"),
                    @Mapping(target = "author", source = "author"),
                    @Mapping(target = "source", source = "source"),
                    @Mapping(target = "licenseId", source = "license.id"),
                    @Mapping(target = "licenseName", source = "license.name"),
                    @Mapping(target = "userId", source = "userId"),
                    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToStrings"),
                    @Mapping(target = "url", source = "files", qualifiedByName = "photoFileToUrl"),
                    @Mapping(target = "colorizedUrl", source = "files", qualifiedByName = "colorizedFileToUrl"),
                    @Mapping(target = "coordinates", source = "coordinates", qualifiedByName = "coordinatesToCoordinatesDTO"),
                    @Mapping(target = "yearRange", source = "yearRange", qualifiedByName = "yearRangeToYearRangeDTO"),
                    @Mapping(target = "createdAt", source = "createdAt"),
                    @Mapping(target = "updatedAt", source = "updatedAt")
            }
    )
    void mapPhotoToDto(PhotoDetails photo, @MappingTarget CommonPhotoDetailsDTO photoDetailsDTO);

}