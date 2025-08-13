package com.dododo.dataox.core.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class HashServiceTest {

    public static final String HEX_DIGITS = "0123456789abcdef";

    @Autowired
    private HashService service;

    @Test
    void testPrepareHashShouldReturnString() {
        String result = service.prepareHash("test");

        assertEquals(64, result.length());

        for (int i = 0; i < result.length(); i++) {
            assertTrue(HEX_DIGITS.contains(result.substring(i, i + 1)));
        }
    }

    @Test
    void testPrepareHashWhenArgumentIsNullShouldThrowException() {
        assertThrows(NullPointerException.class, () -> service.prepareHash(null));
    }

    @Test
    void testPrepareNoSaltHashShouldReturnString() {
        String result = service.prepareNoSaltHash("test");

        assertEquals(64, result.length());

        for (int i = 0; i < result.length(); i++) {
            assertTrue(HEX_DIGITS.contains(result.substring(i, i + 1)));
        }
    }

    @Test
    void testPrepareNoSaltHashWhenArgumentIsNullShouldThrowException() {
        assertThrows(NullPointerException.class, () -> service.prepareNoSaltHash(null));
    }
}
