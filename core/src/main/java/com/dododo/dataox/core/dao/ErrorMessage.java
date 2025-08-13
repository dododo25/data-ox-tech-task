package com.dododo.dataox.core.dao;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class ErrorMessage {

    private String message;

    private Date createdAt;

    public ErrorMessage(String message) {
        this(message, new Date());
    }
}