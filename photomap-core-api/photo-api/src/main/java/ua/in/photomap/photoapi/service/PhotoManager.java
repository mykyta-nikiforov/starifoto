package ua.in.photomap.photoapi.service;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;
import ua.in.photomap.photoapi.dto.PhotoDetailsDTO;
import ua.in.photomap.photoapi.dto.PhotoMetadataDTO;
import ua.in.photomap.photoapi.dto.PhotoSitemapDataDTO;
import ua.in.photomap.photoapi.dto.PhotoUpdateMetadataDTO;
import ua.in.photomap.photoapi.dto.PhotoThumbnailDTO;
import ua.in.photomap.photoapi.model.Photo;

import java.io.File;
import java.util.List;

public interface PhotoManager {

    PhotoDetailsDTO getPhotoDetails(Long id);

    Photo uploadPhoto(PhotoMetadataDTO metadata, List<File> files);

    void deletePhoto(Long photoId);

    Photo updatePhoto(Long id, PhotoUpdateMetadataDTO metadata, List<MultipartFile> files);

    void addComment(Long photoId, String text);

    Page<PhotoDetailsDTO> getAll(int page, int size);

    Page<PhotoSitemapDataDTO> getAllForSitemap(int page, int size);

    List<PhotoThumbnailDTO> getThumbnailUrlsByPhotoIds(List<Long> photoIds);
}
