package com.dododo.dataox.core.exception;

import org.springframework.http.HttpStatus;

public class UnsuccessfulLoginException extends ControllerException {

    public UnsuccessfulLoginException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
