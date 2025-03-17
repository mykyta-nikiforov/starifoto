package ua.in.photomap.photoapi.service.impl;

import com.google.cloud.storage.Blob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import ua.in.photomap.common.rest.toolkit.exception.ResourceNotFoundException;
import ua.in.photomap.photoapi.constant.StorageFolder;
import ua.in.photomap.photoapi.constant.ThumbDimensions;
import ua.in.photomap.photoapi.exception.FileUploadInternalException;
import ua.in.photomap.photoapi.model.ImageFile;
import ua.in.photomap.photoapi.model.ImageFileType;
import ua.in.photomap.photoapi.repository.ImageFileRepository;
import ua.in.photomap.photoapi.service.CloudStorageService;
import ua.in.photomap.photoapi.service.ImageFileService;
import ua.in.photomap.photoapi.service.ThumbnailGenerationService;
import ua.in.photomap.photoapi.util.phash.ImagePHashFactory;
import ua.in.photomap.photoapi.dto.PhotoThumbnailDTO;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageFileServiceImpl implements ImageFileService {
    private final ImageFileRepository imageFileRepository;
    private final CloudStorageService cloudStorageService;
    private final ThumbnailGenerationService thumbnailGenerationService;
    private final ImagePHashFactory imagePHashFactory;

    @Override
    public ImageFile uploadFile(File file, ImageFileType fileType) {
        log.info("Uploading file to File Storage. File name: " + file.getName());

        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(bytes));
            int width = image.getWidth();
            int height = image.getHeight();
            String fileName = file.getName();
            String filePath = StorageFolder.PHOTOS.getPath();
            Blob blob = cloudStorageService.uploadFile(bytes, fileName, filePath);

            String hash = null;
            if (fileType == ImageFileType.ORIGINAL) {
                hash = imagePHashFactory.getInstance().getHash(new ByteArrayInputStream(bytes));
            }

            return handleUploadedFileResponse(blob.getName(), buildLink(blob), height, width, fileType, hash);
        } catch (IOException e) {
            throw new FileUploadInternalException("Error while reading the image file.", e);
        }
    }

    @Override
    public ImageFile generateThumbnailByFileName(String fileName) {
        log.info("Creating thumbnail for file: {}", fileName);
        Blob blob = cloudStorageService.getFile(fileName);
        byte[] thumbnail = thumbnailGenerationService.generate(blob.getContent());
        Blob uploadedBlob = cloudStorageService.uploadFile(thumbnail, fileName, StorageFolder.THUMBNAILS.getPath());
        return handleUploadedFileResponse(uploadedBlob.getName(),
                buildLink(uploadedBlob),
                ThumbDimensions.WIDTH_IN_PIXELS.getValue(),
                ThumbDimensions.WIDTH_IN_PIXELS.getValue(), ImageFileType.THUMBNAIL,
                null);
    }

    private ImageFile handleUploadedFileResponse(String fileName,
                                                 String fileUrl,
                                                 Integer height,
                                                 Integer width,
                                                 ImageFileType fileType,
                                                 String pHash) {
        ImageFile imageFile = new ImageFile();
        imageFile.setName(fileName);
        imageFile.setUrl(fileUrl);
        imageFile.setHeight(height.shortValue());
        imageFile.setWidth(width.shortValue());
        imageFile.setFileType(fileType);
        imageFile.setImagePHash(pHash);
        return imageFileRepository.save(imageFile);
    }

    @Override
    public void delete(Long id) {
        ImageFile imageFile = imageFileRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Photo file not found"));

        imageFileRepository.delete(imageFile);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        if (cloudStorageService.deleteFile(imageFile.getName())) {
            log.info("File deleted from File Storage. File name: " + imageFile.getName());
        } else {
            log.error("Can't delete file from File Storage. File name: " + imageFile.getName());
        }
    }

    @Override
    public List<Long> getPhotoFilesIdsByPhotoId(Long photoId) {
        return imageFileRepository.findAllByPhotoId(photoId);
    }

    @Override
    public List<PhotoThumbnailDTO> getThumbnailUrlsByPhotoIds(List<Long> photoIds) {
        return imageFileRepository.findPhotoIdAndThumbnailUrlByPhotoIds(photoIds, ImageFileType.THUMBNAIL);
    }

    private String buildLink(Blob blob) {
        return "https://storage.googleapis.com/" + blob.getBucket() + "/" + blob.getName();
    }
}
