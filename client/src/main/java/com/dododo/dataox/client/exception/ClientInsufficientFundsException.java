package com.dododo.dataox.client.exception;

import com.dododo.dataox.core.exception.ControllerException;
import org.springframework.http.HttpStatus;

public class ClientInsufficientFundsException extends ControllerException {

    public ClientInsufficientFundsException(Long id) {
        super(HttpStatus.BAD_REQUEST, String.format("insufficient funds for client with id %s", id));
    }
}
