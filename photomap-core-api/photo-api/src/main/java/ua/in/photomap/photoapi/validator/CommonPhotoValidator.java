package ua.in.photomap.photoapi.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import ua.in.photomap.common.rest.toolkit.exception.ValidationException;
import ua.in.photomap.photoapi.dto.PhotoMetadataDTO;
import ua.in.photomap.photoapi.service.LicenseService;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class CommonPhotoValidator {
    private final LicenseService licenseService;

    private static final String[] ALLOWED_CONTENT_TYPES = {
            "image/jpeg"
    };

    @Value("${photomap.file.max-size-in-mb}")
    private Long maxFileSizeInMb;

    public void validateMetadata(PhotoMetadataDTO metadata) {
        if (ObjectUtils.isEmpty(metadata)) {
            throw new ValidationException("Metadata is required");
        } else if (metadata.getLicenseId() == null) {
            throw new ValidationException("License is required");
        } else if (licenseService.findById(metadata.getLicenseId()).isEmpty()) {
            throw new ValidationException("License with id " + metadata.getLicenseId() + " does not exist");
        } else if (metadata.getYearRange() == null || metadata.getYearRange().getStart() == null
                || metadata.getYearRange().getEnd() == null) {
            throw new ValidationException("Year range is required");
        } else if (metadata.getTags() == null || metadata.getTags().isEmpty()
                || metadata.getTags().stream().anyMatch(ObjectUtils::isEmpty)) {
            throw new ValidationException("Tags cannot be empty");
        } else if (metadata.getCoordinates() == null || metadata.getCoordinates().getLatitude() == null
                || metadata.getCoordinates().getLongitude() == null) {
            throw new ValidationException("Coordinates are required");
        } else if (metadata.getCoordinates().getIsApproximate() == null) {
            throw new ValidationException("Coordinates approximation is required");
        }
    }

    public void validateFile(MultipartFile file) {
        if (ObjectUtils.isEmpty(file)) {
            throw new ValidationException("File is empty.");
        }
        if (!Arrays.asList(ALLOWED_CONTENT_TYPES).contains(file.getContentType())) {
            throw new ValidationException("Invalid file type. Only images are allowed.");
        }

        if (file.getSize() > getMaxFileSizeInBytes()) {
            throw new ValidationException("File size exceeds the allowed limit of " + maxFileSizeInMb + "MB");
        }
    }

    private Long getMaxFileSizeInBytes() {
        return maxFileSizeInMb * 1024 * 1024;
    }

}
