package ua.in.photomap.photoapi.service;

import com.google.cloud.storage.Blob;

public interface CloudStorageService {

    Blob uploadFile(byte[] fileContent, String fileName, String folder);

    boolean deleteFile(String fileName);

    Blob getFile(String filePath);
}
