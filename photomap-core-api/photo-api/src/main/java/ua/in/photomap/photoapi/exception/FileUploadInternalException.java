package ua.in.photomap.photoapi.exception;

public class FileUploadInternalException extends RuntimeException {

    public FileUploadInternalException(String message) {
        super(message);
    }

    public FileUploadInternalException(String message, Throwable cause) {
        super(message, cause);
    }
}