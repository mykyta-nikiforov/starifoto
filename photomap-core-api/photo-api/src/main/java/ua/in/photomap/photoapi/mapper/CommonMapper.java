package ua.in.photomap.photoapi.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingInheritanceStrategy;
import org.mapstruct.Named;
import ua.in.photomap.common.photo.model.dto.CoordinatesDTO;
import ua.in.photomap.common.photo.model.dto.YearRangeDTO;
import ua.in.photomap.photoapi.model.*;
import ua.in.photomap.photoapi.util.FileUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(mappingInheritanceStrategy = MappingInheritanceStrategy.AUTO_INHERIT_FROM_CONFIG)
public interface CommonMapper {

    @Named("tagsToStrings")
    default List<String> tagsToStrings(Set<Tag> tags) {
        return tags.stream()
                .map(Tag::getName)
                .sorted()
                .collect(Collectors.toList());
    }

    @Named("photoFileToUrl")
    default String photoFileToUrl(Set<ImageFile> files) {
        return getUrlByFileType(files, ImageFileType.ORIGINAL);
    }

    @Named("thumbnailFileToUrl")
    default String thumbnailFileToUrl(Set<ImageFile> files) {
        return getUrlByFileType(files, ImageFileType.THUMBNAIL);
    }

    @Named("colorizedFileToUrl")
    default String colorizedFileToUrl(Set<ImageFile> files) {
        return getUrlByFileType(files, ImageFileType.COLORIZED);
    }

    @Named("coordinatesToCoordinatesDTO")
    default CoordinatesDTO coordinatesToCoordinatesDTO(Coordinates coordinates) {
        return new CoordinatesDTO(coordinates.getLatitude(), coordinates.getLongitude(), coordinates.getIsApproximate());
    }

    @Named("yearRangeToYearRangeDTO")
    default YearRangeDTO yearRangeToYearRangeDTO(YearRange yearRange) {
        return new YearRangeDTO(yearRange.getStart(), yearRange.getEnd());
    }

    private static String getUrlByFileType(Set<ImageFile> files, ImageFileType fileType) {
        return FileUtils.getImageFileByType(files, fileType)
                .map(ImageFile::getUrl)
                .orElse(null);
    }
}