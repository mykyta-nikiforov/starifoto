package ua.in.photomap.photoapi.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ua.in.photomap.common.rest.toolkit.util.DiffUtils;
import ua.in.photomap.photoapi.dto.PhotoMetadataDTO;
import ua.in.photomap.photoapi.model.*;
import ua.in.photomap.photoapi.service.LicenseService;
import ua.in.photomap.photoapi.service.TagService;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class PhotoMetadataMapper {
    private final TagService tagService;
    private final LicenseService licenseService;

    public void setPhotoDetails(Photo photo, PhotoMetadataDTO metadata) {
        if (DiffUtils.areDifferent(photo.getTitle(), metadata.getTitle())) {
            photo.setTitle(metadata.getTitle());
        }
        if (DiffUtils.areDifferent(photo.getDescription(), metadata.getDescription())) {
            photo.setDescription(metadata.getDescription());
        }
        if (DiffUtils.areDifferent(photo.getAuthor(), metadata.getAuthor())) {
            photo.setAuthor(metadata.getAuthor());
        }
        if (DiffUtils.areDifferent(photo.getSource(), metadata.getSource())) {
            photo.setSource(metadata.getSource());
        }
        if (photo.getTags() == null
                || DiffUtils.areDifferent(new HashSet<>(photo.getTags()), new HashSet<>(metadata.getTags()))) {
            Set<Tag> tags = tagService.getExistingTagsOrCreateNew(metadata.getTags());
            photo.setTags(tags);
        }
        if (photo.getYearRange() == null
                || DiffUtils.areDifferent(photo.getYearRange().getStart(), metadata.getYearRange().getStart())
                || DiffUtils.areDifferent(photo.getYearRange().getEnd(), metadata.getYearRange().getEnd())) {
            photo.setYearRange(new YearRange(metadata.getYearRange().getStart(), metadata.getYearRange().getEnd()));
        }
        if (photo.getCoordinates() == null
                || DiffUtils.areDifferent(photo.getCoordinates().getLatitude(), metadata.getCoordinates().getLatitude())
                || DiffUtils.areDifferent(photo.getCoordinates().getLongitude(), metadata.getCoordinates().getLongitude())
                || DiffUtils.areDifferent(photo.getCoordinates().getIsApproximate(), metadata.getCoordinates().getIsApproximate())
        ) {
            photo.setCoordinates(new Coordinates(metadata.getCoordinates().getLatitude(), metadata.getCoordinates().getLongitude(),
                    metadata.getCoordinates().getIsApproximate()));
        }
        if (photo.getLicense() == null || DiffUtils.areDifferent(photo.getLicense().getId(), metadata.getLicenseId())) {
            License license = licenseService.getById(metadata.getLicenseId());
            photo.setLicense(license);
        }
    }
}
