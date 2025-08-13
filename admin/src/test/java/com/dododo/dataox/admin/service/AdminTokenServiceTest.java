package com.dododo.dataox.admin.service;

import com.dododo.dataox.core.model.Admin;
import com.dododo.dataox.core.model.AdminAuthToken;
import com.dododo.dataox.core.repository.AdminTokenRepository;
import com.dododo.dataox.core.service.HashService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AdminTokenServiceTest {

    @Mock
    private AdminTokenRepository tokenRepository;

    @Mock
    private HashService hashService;

    @InjectMocks
    private AdminTokenService tokenService;

    @Test
    void testFindClientByTokenShouldReturnObject() {
        Admin expectedAdmin = mock(Admin.class);

        AdminAuthToken authToken = mock(AdminAuthToken.class);

        when(authToken.getAdmin())
                .thenReturn(expectedAdmin);
        when(authToken.getExpirationDate())
                .thenReturn(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findById("test"))
                .thenReturn(Optional.of(authToken));

        Admin output = tokenService.findAdminByToken("test");

        assertEquals(expectedAdmin, output);
    }

    @Test
    void testFindClientByTokenWhenTokenIsNullShouldReturnNull() {
        assertNull(tokenService.findAdminByToken("test"));
    }

    @Test
    void testFindClientByTokenWhenTokenIsExpiredShouldReturnNull() {
        AdminAuthToken authToken = mock(AdminAuthToken.class);

        when(authToken.getExpirationDate())
                .thenReturn(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findById("test"))
                .thenReturn(Optional.of(authToken));

        assertNull(tokenService.findAdminByToken("test"));
    }

    @Test
    void testCreateForShouldReturnObject() {
        String expected = "test";

        when(hashService.prepareNoSaltHash(any()))
                .thenReturn(expected);

        String output = tokenService.createFor(mock());

        verify(tokenRepository, times(1))
                .save(any());

        assertEquals(expected, output);
    }
}
