package ua.in.photomap.user.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ua.in.photomap.common.rest.toolkit.exception.InternalException;
import ua.in.photomap.common.rest.toolkit.exception.ValidationException;
import ua.in.photomap.common.rest.toolkit.exception.handler.GlobalErrorHandler;
import ua.in.photomap.user.exception.EmailUsedException;
import ua.in.photomap.user.exception.UnconfirmedUserException;

import java.util.stream.Collectors;

@ControllerAdvice
@Slf4j
public class UserApiErrorHandler extends GlobalErrorHandler {

    @ExceptionHandler({InternalException.class})
    public ResponseEntity<Object> customHandleNotFound(Exception ex, WebRequest request) {
        return customHandleErrorCode(ex, request, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({EmailUsedException.class})
    public ResponseEntity<Object> customHandleEmailUsed(Exception ex, WebRequest request) {
        return customHandleErrorCode(ex, request, HttpStatus.CONFLICT);
    }

    @ExceptionHandler({UnconfirmedUserException.class})
    public ResponseEntity<Object> customHandleUnconfirmedUser(Exception ex, WebRequest request) {
        return customHandleErrorCode(ex, request, HttpStatus.I_AM_A_TEAPOT);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatusCode status,
                                                                  WebRequest request) {
        ValidationException exception = new ValidationException(buildValidationErrorMessage(ex), ex);
        return customHandleErrorCode(exception, request, HttpStatus.BAD_REQUEST);
    }

    private String buildValidationErrorMessage(MethodArgumentNotValidException ex) {
        return ex.getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.joining(". "));
    }
}
