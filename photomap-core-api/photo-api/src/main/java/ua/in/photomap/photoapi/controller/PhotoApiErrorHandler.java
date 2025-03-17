package ua.in.photomap.photoapi.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ua.in.photomap.common.rest.toolkit.exception.handler.GlobalErrorHandler;
import ua.in.photomap.photoapi.dto.PhotoDTO;
import ua.in.photomap.photoapi.exception.SimilarPhotosExistException;

import java.util.List;

@ControllerAdvice
@Slf4j
public class PhotoApiErrorHandler extends GlobalErrorHandler {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> customHandleNotFound(Exception ex, WebRequest request) {
        log.error("Exception has been thrown: ", ex);
        return customHandleErrorCode(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({SimilarPhotosExistException.class})
    public ResponseEntity<Object> customHandleSimilarPhotosExist(SimilarPhotosExistException ex, WebRequest request) {
        List<Long> similarPhotosIds = ex.getSimilarPhotos().stream().map(PhotoDTO::getId).toList();
        log.info("SimilarPhotosExistException has been thrown. Similar photos ids: {}", similarPhotosIds);
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ex.getSimilarPhotos());
    }
}
