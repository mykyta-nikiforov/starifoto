package ua.in.photomap.photoapi.migration.script;

import com.google.cloud.storage.Blob;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ua.in.photomap.photoapi.model.ImageFile;
import ua.in.photomap.photoapi.repository.ImageFileRepository;
import ua.in.photomap.photoapi.service.CloudStorageService;
import ua.in.photomap.photoapi.util.phash.ImagePHashFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class M001_Set_Images_PHash implements Script {
    private final CloudStorageService cloudStorageService;
    private final ImagePHashFactory imagePHashFactory;
    private final ImageFileRepository imageFileRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void start() {
        log.info("M001_Set_Images_PHash started");
        String selectQuery = "SELECT i FROM ImageFile i WHERE i.imagePHash IS NULL AND i.fileType = 'ORIGINAL'";
        List<ImageFile> imagesToProcess = entityManager.createQuery(selectQuery, ImageFile.class).getResultList();
        List<ImageFile> processedImages = imagesToProcess.stream()
                .map(this::addPHash)
                .filter(image -> image.getImagePHash() != null)
                .toList();
        imageFileRepository.saveAll(processedImages);
        log.info("Added pHash to {} images. Skipped: {}", processedImages.size(), imagesToProcess.size() - processedImages.size());
    }

    private ImageFile addPHash(ImageFile image) {
        Blob blob = cloudStorageService.getFile(image.getName());
        try {
            String pHash = imagePHashFactory.getInstance().getHash(new ByteArrayInputStream(blob.getContent()));
            image.setImagePHash(pHash);
            log.info("Added pHash to image: {}", image.getName());
        } catch (IOException e) {
            log.error("Error while processing image: " + image.getName(), e);
        }
        return image;
    }

    @Override
    public int getVersion() {
        return 1;
    }
}
