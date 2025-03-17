package ua.in.photomap.photoapi.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;
import ua.in.photomap.common.photo.model.dto.UserBasicDTO;
import ua.in.photomap.common.rest.toolkit.service.JwtAuthenticatedUserResolver;
import ua.in.photomap.common.rest.toolkit.util.DiffUtils;
import ua.in.photomap.photoapi.dto.PhotoDetailsDTO;
import ua.in.photomap.photoapi.dto.PhotoMetadataDTO;
import ua.in.photomap.photoapi.dto.PhotoSitemapDataDTO;
import ua.in.photomap.photoapi.dto.PhotoUpdateMetadataDTO;
import ua.in.photomap.photoapi.mapper.PhotoMapper;
import ua.in.photomap.photoapi.mapper.PhotoMetadataMapper;
import ua.in.photomap.photoapi.model.Photo;
import ua.in.photomap.photoapi.service.*;
import ua.in.photomap.photoapi.util.FileUtils;
import ua.in.photomap.photoapi.dto.PhotoThumbnailDTO;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class PhotoManagerImpl implements PhotoManager {
    private final PhotoService photoService;
    private final ImageFileService imageFileService;
    private final KafkaService kafkaService;
    private final PhotoMetadataMapper photoMetadataMapper;
    private final PhotoFileUploadService photoFileUploadService;
    private final CommentService commentService;
    private final UserApiService userApiService;

    @Override
    public PhotoDetailsDTO getPhotoDetails(Long id) {
        Photo photo = photoService.getById(id);
        PhotoDetailsDTO result = PhotoMapper.INSTANCE.photoToPhotoDetailsDto(photo);
        userApiService.findById(photo.getUserId())
                .ifPresent(user -> result.setUserName(user.getName()));
        return result;
    }

    @Override
    @Transactional
    public Photo uploadPhoto(PhotoMetadataDTO metadata, List<File> files) {
        Photo saved = photoService.save(buildPhoto(metadata));
        CompletableFuture.runAsync(() -> {
            Photo photo = photoFileUploadService.uploadNewPhotoFiles(saved, files);
            PhotoGeoJsonDataDTO photoGeoDTO = PhotoMapper.INSTANCE.photoToGeoJsonDataDto(photo);
            kafkaService.pushNewPhotoEvent(photoGeoDTO);
        });
        return saved;
    }

    private Photo buildPhoto(PhotoMetadataDTO metadata) {
        Photo photo = new Photo();
        photoMetadataMapper.setPhotoDetails(photo, metadata);
        photo.setUserId(JwtAuthenticatedUserResolver.getCurrentUser().getId());
        photo.setFiles(new HashSet<>());
        return photo;
    }

    @Override
    @Transactional
    public void deletePhoto(Long photoId) {
        commentService.deleteAllByPhotoId(photoId);
        photoService.deleteById(photoId);
        CompletableFuture.runAsync(() -> {
            imageFileService.getPhotoFilesIdsByPhotoId(photoId)
                    .forEach(imageFileService::delete);
            kafkaService.pushDeletePhotoEvent(photoId);
        });
    }

    @Override
    @Transactional
    public Photo updatePhoto(Long id, PhotoUpdateMetadataDTO metadata, List<MultipartFile> multipartFiles) {
        Photo existingPhoto = photoService.getById(id);
        photoMetadataMapper.setPhotoDetails(existingPhoto, metadata);
        Photo saved = photoService.save(existingPhoto);
        if (geoJsonRelatedDataChanged(existingPhoto, metadata)) {
            PhotoGeoJsonDataDTO photoGeoDTO = PhotoMapper.INSTANCE.photoToGeoJsonDataDto(saved);
            kafkaService.pushUpdatePhotoEvent(photoGeoDTO);
        }
        if (!CollectionUtils.isEmpty(metadata.getChangedImageTypes())) {
            CompletableFuture.runAsync(() -> photoFileUploadService.savePhotoFiles(saved,
                    convertMultiPartToFiles(multipartFiles),
                    metadata.getChangedImageTypes())
            );
        }
        return saved;
    }

    @Override
    @Transactional
    public void addComment(Long photoId, String text) {
        Photo photo = photoService.getById(photoId);
        commentService.create(photo, text);
    }

    @Override
    public Page<PhotoDetailsDTO> getAll(int page, int size) {
        Page<Photo> photos = photoService.getAll(page, size);
        List<Long> userIds = photos.stream()
                .map(Photo::getUserId)
                .toList();
        Map<Long, String> userNames = userApiService.findAllBasicByIds(userIds).stream()
                .collect(Collectors.toMap(UserBasicDTO::getId, UserBasicDTO::getName));
        return photos.map(photo -> {
            PhotoDetailsDTO dto = PhotoMapper.INSTANCE.photoToPhotoDetailsDto(photo);
            dto.setUserName(userNames.get(photo.getUserId()));
            return dto;
        });
    }

    @Override
    public Page<PhotoSitemapDataDTO> getAllForSitemap(int page, int size) {
        return photoService.getAllForSitemap(page, size);
    }

    private static List<File> convertMultiPartToFiles(List<MultipartFile> multipartFiles) {
        return Optional.ofNullable(multipartFiles)
                .orElseGet(ArrayList::new)
                .stream()
                .map(FileUtils::convertMultiPartToFile)
                .toList();
    }

    public boolean geoJsonRelatedDataChanged(Photo original, PhotoMetadataDTO metadata) {
        return DiffUtils.areDifferent(original.getCoordinates().getLatitude(), metadata.getCoordinates().getLatitude()) ||
                DiffUtils.areDifferent(original.getCoordinates().getLongitude(), metadata.getCoordinates().getLongitude()) ||
                DiffUtils.areDifferent(original.getYearRange().getStart(), metadata.getYearRange().getStart()) ||
                DiffUtils.areDifferent(original.getYearRange().getEnd(), metadata.getYearRange().getEnd()) ||
                DiffUtils.areDifferent(new HashSet<>(original.getTags()), new HashSet<>(metadata.getTags()));
    }

    @Override
    public List<PhotoThumbnailDTO> getThumbnailUrlsByPhotoIds(List<Long> photoIds) {
        return imageFileService.getThumbnailUrlsByPhotoIds(photoIds);
    }
}
