package ua.in.photomap.photoapi.validator;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import ua.in.photomap.common.rest.toolkit.exception.ValidationException;
import ua.in.photomap.common.rest.toolkit.model.JwtAuthenticatedUser;
import ua.in.photomap.common.rest.toolkit.service.JwtAuthenticatedUserResolver;
import ua.in.photomap.photoapi.dto.PhotoDTO;
import ua.in.photomap.photoapi.dto.PhotoMetadataDTO;
import ua.in.photomap.photoapi.dto.PhotoUpdateMetadataDTO;
import ua.in.photomap.photoapi.exception.SimilarPhotosExistException;
import ua.in.photomap.photoapi.mapper.PhotoMapper;
import ua.in.photomap.photoapi.model.Photo;
import ua.in.photomap.photoapi.model.SimilarPhotoData;
import ua.in.photomap.photoapi.repository.PhotoRepository;
import ua.in.photomap.photoapi.util.phash.ImagePHashFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.file.Files;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PhotoValidator {
    private final PhotoRepository photoRepository;
    private final CommonPhotoValidator commonPhotoValidator;
    private final ImagePHashFactory imagePHashFactory;
    private static final int GET_ICONS_REQUEST_PHOTO_IDS_LIMIT = 500;

    public void validateCreateRequest(PhotoMetadataDTO metadata, List<MultipartFile> filesList) {
        commonPhotoValidator.validateMetadata(metadata);
        if (filesList.isEmpty()) {
            throw new ValidationException("File is required");
        }
        filesList.forEach(commonPhotoValidator::validateFile);
    }

    public void validateUpdateRequest(Long id, PhotoUpdateMetadataDTO metadata, List<MultipartFile> files) {
        validateAccess(id);
        commonPhotoValidator.validateMetadata(metadata);
        if (!CollectionUtils.isEmpty(files)) {
            files.forEach(commonPhotoValidator::validateFile);
        }
    }

    public void validateDeleteRequest(Long photoId) {
        validateAccess(photoId);
    }

    private void validateAccess(Long photoId) {
        if (photoId == null) {
            throw new ValidationException("Photo id is required");
        }
        JwtAuthenticatedUser currentUser = JwtAuthenticatedUserResolver.getCurrentUser();
        if (!currentUser.isModeratorOrAdmin() && !photoRepository.existsByIdAndUserId(photoId, currentUser.getId())) {
            throw new ValidationException("You are not allowed to operate with photo with id " + photoId);
        }
    }

    public void validateOnDuplicates(List<File> files) {
        if (files.isEmpty()) {
            throw new ValidationException("File is required");
        }
        File file = files.get(0);
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            String pHash = imagePHashFactory.getInstance().getHash(new ByteArrayInputStream(bytes));
            List<Long> similarPhotosByHash = photoRepository.findSimilarPhotosByHash(pHash).stream()
                    .map(SimilarPhotoData::getId)
                    .sorted()
                    .toList();
            List<Photo> similarPhotos = photoRepository.findAllGraphByIds(similarPhotosByHash);
            if (!similarPhotosByHash.isEmpty()) {
                List<PhotoDTO> content = similarPhotos.stream()
                        .map(PhotoMapper.INSTANCE::photoToPhotoDto)
                        .toList();
                throw new SimilarPhotosExistException("File with the same content already exists", content);
            }
        } catch (SimilarPhotosExistException e) {
            file.delete();
            throw e;
        } catch (Exception e) {
            file.delete();
            log.error("Failed to validate file", e);
            throw new ValidationException("Failed to validate file", e);
        }

    }

    public void validateGetPhotoIconsRequest(List<Long> photoIds) {
        if (photoIds.size() > GET_ICONS_REQUEST_PHOTO_IDS_LIMIT) {
            throw new ValidationException("The number of photo IDs cannot exceed " + GET_ICONS_REQUEST_PHOTO_IDS_LIMIT);
        }
    }
}
