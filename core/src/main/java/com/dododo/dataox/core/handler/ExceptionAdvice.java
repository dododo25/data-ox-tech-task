package com.dododo.dataox.core.handler;

import com.dododo.dataox.core.dao.ErrorMessage;
import com.dododo.dataox.core.exception.ControllerException;
import io.swagger.v3.oas.annotations.Hidden;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Hidden
@RestControllerAdvice
public class ExceptionAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorMessage> handleException(Exception e, HttpServletRequest request) {
        ErrorMessage response = new ErrorMessage(e.getMessage());
        LOGGER.warn("{} {} - {}", request.getMethod(), request.getRequestURI(), e.getMessage());

        if (e instanceof ControllerException) {
            return new ResponseEntity<>(response, ((ControllerException) e).getStatus());
        }

        return new ResponseEntity<>(response, BAD_REQUEST);
    }
}