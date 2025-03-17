package ua.in.photomap.photoapi.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ua.in.photomap.photoapi.model.ImageFile;
import ua.in.photomap.photoapi.model.ImageFileType;
import ua.in.photomap.photoapi.dto.PhotoThumbnailDTO;

import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {
    @Query("select f.id from Photo p join p.files f where p.id = :photoId")
    List<Long> findAllByPhotoId(Long photoId);

    @Query("SELECT new ua.in.photomap.photoapi.dto.PhotoThumbnailDTO(p.id, f.url) FROM Photo p JOIN p.files f WHERE p.id IN :photoIds AND f.fileType = :fileType")
    List<PhotoThumbnailDTO> findPhotoIdAndThumbnailUrlByPhotoIds(List<Long> photoIds, ImageFileType fileType);
}
