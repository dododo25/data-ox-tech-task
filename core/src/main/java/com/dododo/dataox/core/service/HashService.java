package com.dododo.dataox.core.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class HashService {

    public static final String HEX_DIGITS = "0123456789abcdef";

    private static MessageDigest digest;

    @Value("${salt}")
    private String salt;

    public HashService() {
        initDigest();
    }

    public String prepareNoSaltHash(String value) {
        if (digest == null) {
            throw new RuntimeException();
        }

        if (value == null) {
            throw new NullPointerException();
        }

        return prepare(value);
    }

    public String prepareHash(String value) {
        if (digest == null) {
            throw new RuntimeException();
        }

        if (value == null) {
            throw new NullPointerException();
        }

        return prepare(value + salt);
    }

    private String prepare(String value) {
        byte[] arr = digest.digest(value.getBytes());

        StringBuilder result = new StringBuilder();

        for (byte b : arr) {
            result.append(HEX_DIGITS.charAt((b >>> 4) & 0x0F))
                    .append(HEX_DIGITS.charAt(b & 0x0F));
        }

        return result.toString();
    }

    private static void initDigest() {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
