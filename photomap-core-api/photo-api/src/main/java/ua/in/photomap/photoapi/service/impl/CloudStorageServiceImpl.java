package ua.in.photomap.photoapi.service.impl;

import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ua.in.photomap.photoapi.service.CloudStorageService;

import java.time.ZonedDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CloudStorageServiceImpl implements CloudStorageService {
    private final Storage storage;

    @Value("${google.cloud.storage.bucketName}")
    private String bucketName;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Override
    public Blob uploadFile(byte[] fileData, String fileName, String folder) {
        log.info("Uploading to CGS Bucket: " + bucketName + " File: " + fileName + " Size: " + getSizeInMb(fileData));
        String generatedFileName = generateFileName(fileName);
        BlobId blobId = buildNewFileBlob(generatedFileName, folder);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType("image/jpeg")
                .build();
        return storage.create(blobInfo, fileData);
    }

    @Override
    public boolean deleteFile(String filePath) {
        log.info("Deleting file: " + filePath);
        BlobId blobId = buildExistingFileBlob(filePath);
        boolean deleted = storage.delete(blobId);
        if (deleted) {
            log.info("Deleted file: " + filePath);
        } else {
            log.info("File not found: " + filePath);
        }
        return deleted;
    }

    @Override
    public Blob getFile(String filePath) {
        log.info("Getting file: " + filePath);
        BlobId blobId = buildExistingFileBlob(filePath);
        return storage.get(blobId);
    }

    private BlobId buildNewFileBlob(String fileName, String folder) {
        String name = folder + fileName;
        if (!activeProfile.contains("prod")) {
            name = "test/" + name;
        }
        return BlobId.of(bucketName, name);
    }

    private BlobId buildExistingFileBlob(String filePath) {
        return BlobId.of(bucketName, filePath);
    }

    private String generateFileName(String originalName) {
        String extension = originalName.substring(originalName.lastIndexOf(".") + 1);
        return "%s_%d.%s"
                .formatted(
                        UUID.randomUUID().toString().toLowerCase().replaceAll("-", ""),
                        ZonedDateTime.now().toInstant().getEpochSecond(),
                        extension
                );
    }

    private static String getSizeInMb(byte[] fileData) {
        int fileSizeInBytes = fileData.length;
        double fileSizeInMB = (double) fileSizeInBytes / (1024 * 1024);
        return String.format("%.2f MB", fileSizeInMB);
    }
}
