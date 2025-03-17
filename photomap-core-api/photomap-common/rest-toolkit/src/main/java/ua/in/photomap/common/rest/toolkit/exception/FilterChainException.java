package ua.in.photomap.common.rest.toolkit.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class FilterChainException extends Exception {
    private final HttpStatus httpStatus;
    public FilterChainException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
}
