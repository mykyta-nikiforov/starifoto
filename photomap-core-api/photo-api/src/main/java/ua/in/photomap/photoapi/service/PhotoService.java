package ua.in.photomap.photoapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ua.in.photomap.common.photo.model.dto.PhotoGeoJsonDataDTO;
import ua.in.photomap.photoapi.dto.PhotoSitemapDataDTO;
import ua.in.photomap.photoapi.model.Photo;

import java.util.List;
import java.util.Optional;

public interface PhotoService {

    Optional<Photo> findById(Long id);

    Photo getById(Long id);

    Photo save(Photo photo);

    void saveAll(List<Photo> photos);

    Page<Photo> getAll(int page, int size);

    Page<PhotoGeoJsonDataDTO> getGeoJsonDataPage(int page, int size);

    Page<Photo> getAllByUserId(Long userId, Pageable pageable);

    void deleteById(Long photoId);

    Page<PhotoSitemapDataDTO> getAllForSitemap(int page, int size);
}
