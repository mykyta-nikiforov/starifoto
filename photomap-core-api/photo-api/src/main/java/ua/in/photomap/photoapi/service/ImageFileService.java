package ua.in.photomap.photoapi.service;

import ua.in.photomap.photoapi.model.ImageFile;
import ua.in.photomap.photoapi.model.ImageFileType;
import ua.in.photomap.photoapi.dto.PhotoThumbnailDTO;

import java.io.File;
import java.util.List;

public interface ImageFileService {

    ImageFile uploadFile(File multipartFile, ImageFileType fileType);

    ImageFile generateThumbnailByFileName(String fileName);

    void delete(Long id);

    List<Long> getPhotoFilesIdsByPhotoId(Long photoId);

    List<PhotoThumbnailDTO> getThumbnailUrlsByPhotoIds(List<Long> photoIds);
}
