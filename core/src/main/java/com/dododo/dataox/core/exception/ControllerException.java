package com.dododo.dataox.core.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Objects;

@Getter
public class ControllerException extends RuntimeException {

    private final HttpStatus status;

    public ControllerException(HttpStatus status, String message) {
        super(message);
        this.status = Objects.requireNonNull(status);
    }
}
