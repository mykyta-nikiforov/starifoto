package ua.in.photomap.photoapi.service;


public interface ThumbnailGenerationService {
    byte[] generate(byte[] fileContent);
}
