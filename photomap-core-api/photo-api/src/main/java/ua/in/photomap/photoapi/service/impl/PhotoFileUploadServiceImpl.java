package ua.in.photomap.photoapi.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.in.photomap.common.photo.model.constant.UserPhotoNotificationType;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;
import ua.in.photomap.common.photo.model.dto.websocket.UserPhotoNotificationDTO;
import ua.in.photomap.photoapi.mapper.PhotoMapper;
import ua.in.photomap.photoapi.model.ImageFile;
import ua.in.photomap.photoapi.model.ImageFileType;
import ua.in.photomap.photoapi.model.Photo;
import ua.in.photomap.photoapi.repository.PhotoRepository;
import ua.in.photomap.photoapi.service.ImageFileService;
import ua.in.photomap.photoapi.service.KafkaService;
import ua.in.photomap.photoapi.service.PhotoFileUploadService;
import ua.in.photomap.photoapi.websocket.NotificationSender;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
@Slf4j
public class PhotoFileUploadServiceImpl implements PhotoFileUploadService {
    private final ImageFileService imageFileService;
    private final PhotoRepository photoRepository;
    private final KafkaService kafkaService;
    private final NotificationSender notificationSender;

    @Override
    @Transactional
    public Photo uploadNewPhotoFiles(Photo photo, List<File> files) {
        log.info("Uploading new photo files for photo with id: {}", photo.getId());
        try {
            IntStream.range(0, files.size())
                    .forEach(index -> {
                        ImageFileType fileType = getImageFileTypeByIndex(index);
                        ImageFile photoImageFile = imageFileService.uploadFile(files.get(index), fileType);
                        photo.getFiles().add(photoImageFile);
                        if (fileType == ImageFileType.ORIGINAL) {
                            ImageFile thumbnailFile = imageFileService.generateThumbnailByFileName(photoImageFile.getName());
                            photo.getFiles().add(thumbnailFile);
                        }
                        photoRepository.save(photo);
                    });
        } finally {
            files.forEach(File::delete);
        }
        notifyWebsocket(photo.getId(), photo.getUserId(), UserPhotoNotificationType.PHOTO_UPDATED);
        return photo;
    }

    @Override
    @Transactional
    public void savePhotoFiles(Photo photo, List<File> files, List<ImageFileType> fileTypes) {
        Set<ImageFile> imageFiles = saveImageFiles(photo.getFiles(), files, fileTypes);
        photo.setFiles(imageFiles);
        Photo saved = photoRepository.save(photo);

        notifyWebsocket(photo.getId(), photo.getUserId(), UserPhotoNotificationType.PHOTO_UPDATED);
        boolean hasOriginalFileChange = fileTypes.contains(ImageFileType.ORIGINAL);
        if (hasOriginalFileChange) {
            notifyMongo(saved);
        }
    }

    private Set<ImageFile> saveImageFiles(Set<ImageFile> photoFiles, List<File> files, List<ImageFileType> fileTypes) {
        Set<ImageFile> result = new HashSet<>(photoFiles);

        IntStream.range(0, files.size())
                .forEach(index -> {
                    ImageFileType fileType = fileTypes.get(index);
                    deleteOldFile(result, fileType);
                    ImageFile newFile = imageFileService.uploadFile(files.get(index), fileType);
                    result.add(newFile);
                    if (fileType == ImageFileType.ORIGINAL) {
                        deleteOldFile(result, ImageFileType.THUMBNAIL);
                        ImageFile thumbnailFile = imageFileService.generateThumbnailByFileName(newFile.getName());
                        result.add(thumbnailFile);
                    }
                });
        // In case of Colorized removed
        if (fileTypes.contains(ImageFileType.COLORIZED) && files.size() < fileTypes.size()) {
            deleteOldFile(result, ImageFileType.COLORIZED);
        }
        files.forEach(File::delete);
        return result;
    }

    private void deleteOldFile(Set<ImageFile> photoFiles, ImageFileType fileType) {
        Optional<ImageFile> file = photoFiles.stream()
                .filter(f -> f.getFileType().equals(fileType))
                .findFirst();
        if (file.isPresent()) {
            photoFiles.remove(file.get());
            imageFileService.delete(file.get().getId());
        }
    }

    private void notifyMongo(Photo photo) {
        PhotoGeoJsonDataDTO photoGeoDTO = PhotoMapper.INSTANCE.photoToGeoJsonDataDto(photo);
        kafkaService.pushUpdatePhotoEvent(photoGeoDTO);
    }

    private void notifyWebsocket(Long photoId, Long userId, UserPhotoNotificationType type) {
        UserPhotoNotificationDTO message = UserPhotoNotificationDTO.builder()
                .photoId(photoId)
                .userId(userId)
                .type(type)
                .build();
        notificationSender.sendUserPhotoNotification(message);
    }

    private static ImageFileType getImageFileTypeByIndex(int index) {
        return index == 0 ? ImageFileType.ORIGINAL : ImageFileType.COLORIZED;
    }
}
