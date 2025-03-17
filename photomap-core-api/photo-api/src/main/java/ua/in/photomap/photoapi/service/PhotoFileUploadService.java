package ua.in.photomap.photoapi.service;

import ua.in.photomap.photoapi.model.ImageFileType;
import ua.in.photomap.photoapi.model.Photo;

import java.io.File;
import java.util.List;

public interface PhotoFileUploadService {
    Photo uploadNewPhotoFiles(Photo photo, List<File> files);
    void savePhotoFiles(Photo photo, List<File> files, List<ImageFileType> fileTypes);

}
