package ua.in.photomap.common.rest.toolkit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import ua.in.photomap.common.rest.toolkit.dto.GenericErrorResponse;
import ua.in.photomap.common.rest.toolkit.exception.ForbiddenException;
import ua.in.photomap.common.rest.toolkit.exception.ResourceNotFoundException;
import ua.in.photomap.common.rest.toolkit.exception.UnauthorizedException;
import ua.in.photomap.common.rest.toolkit.exception.ValidationException;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalErrorHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({UnauthorizedException.class, ForbiddenException.class, AccessDeniedException.class})
    public ResponseEntity<Object> handleForbidden(Exception ex, WebRequest request) {
        return customHandleErrorCode(ex, request, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFound(Exception ex, WebRequest request) {
        return customHandleErrorCode(ex, request, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleBadRequest(Exception ex, WebRequest request) {
        return customHandleErrorCode(ex, request, HttpStatus.BAD_REQUEST);
    }

    protected static ResponseEntity<Object> customHandleErrorCode(Exception ex, WebRequest request,
                                                                                HttpStatus status) {
        String resolvedMessage = ex.getLocalizedMessage();
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        String timestamp = LocalDateTime.now().toString();
        HttpHeaders headers = new HttpHeaders();

        GenericErrorResponse errorResponse = new GenericErrorResponse(timestamp, status.value(),
                status.getReasonPhrase(), resolvedMessage, path);
        log.warn("Exception: {}, errorResponse: {}", ex.getClass().getSimpleName(), errorResponse);
        return new ResponseEntity<>(errorResponse, headers, status);
    }
}
