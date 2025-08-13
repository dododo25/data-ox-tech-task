package com.dododo.dataox.client.exception;

import com.dododo.dataox.core.exception.ControllerException;

import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

public class TooManyRequestsException extends ControllerException {

    public TooManyRequestsException() {
        super(TOO_MANY_REQUESTS, "too many requests!");
    }
}
