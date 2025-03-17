package ua.in.photomap.photoapi.exception;

import lombok.Getter;
import ua.in.photomap.photoapi.dto.PhotoDTO;

import java.util.List;

@Getter
public class SimilarPhotosExistException extends RuntimeException {
    private List<PhotoDTO> similarPhotos;

    public SimilarPhotosExistException(String message, List<PhotoDTO> similarPhotos) {
        super(message);
        this.similarPhotos = similarPhotos;
    }

    public SimilarPhotosExistException(String message, Throwable cause) {
        super(message, cause);
    }
}