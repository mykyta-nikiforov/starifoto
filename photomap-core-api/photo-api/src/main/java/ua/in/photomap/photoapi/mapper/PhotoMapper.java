package ua.in.photomap.photoapi.mapper;

import org.mapstruct.InheritConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;
import ua.in.photomap.photoapi.dto.PhotoDTO;
import ua.in.photomap.photoapi.dto.PhotoDetailsDTO;
import ua.in.photomap.photoapi.dto.UserPhotoDTO;
import ua.in.photomap.photoapi.model.Photo;

@Mapper(config = CommonMapperConfig.class)
public interface PhotoMapper {
    PhotoMapper INSTANCE = Mappers.getMapper(PhotoMapper.class);

    @InheritConfiguration(name = "mapPhotoToDto")
    PhotoDTO photoToPhotoDto(Photo entity);

    // TODO Pass username as a parameter
    @InheritConfiguration(name = "mapPhotoToDto")
    PhotoDetailsDTO photoToPhotoDetailsDto(Photo entity);

    @Mapping(target = "status", constant = "APPROVED")
    @InheritConfiguration(name = "mapPhotoToDto")
    UserPhotoDTO approvedPhotoToUserPhotoDto(Photo entity);

    @Mappings(
            {
                    @Mapping(target = "coordinates", source = "coordinates", qualifiedByName = "coordinatesToCoordinatesDTO"),
                    @Mapping(target = "photoId", source = "id"),
                    @Mapping(target = "iconThumbUrl", source = "files", qualifiedByName = "thumbnailFileToUrl"),
                    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagsToStrings"),
                    @Mapping(target = "yearRange", source = "yearRange", qualifiedByName = "yearRangeToYearRangeDTO")
            }
    )
    PhotoGeoJsonDataDTO photoToGeoJsonDataDto(Photo entity);
}