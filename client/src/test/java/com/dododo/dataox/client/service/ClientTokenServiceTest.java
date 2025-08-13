package com.dododo.dataox.client.service;

import com.dododo.dataox.core.model.ClientAuthToken;
import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.core.repository.ClientTokenRepository;
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
public class ClientTokenServiceTest {

    @Mock
    private ClientTokenRepository tokenRepository;

    @Mock
    private HashService hashService;

    @InjectMocks
    private ClientTokenService tokenService;

    @Test
    void testFindClientByTokenShouldReturnObject() {
        Client expectedClient = mock(Client.class);

        ClientAuthToken authToken = mock(ClientAuthToken.class);

        when(authToken.getClient())
                .thenReturn(expectedClient);
        when(authToken.getExpirationDate())
                .thenReturn(LocalDateTime.now().plusHours(1));

        when(tokenRepository.findById("test"))
                .thenReturn(Optional.of(authToken));

        Client output = tokenService.findClientByToken("test");

        assertEquals(expectedClient, output);
    }

    @Test
    void testFindClientByTokenWhenTokenIsNullShouldReturnNull() {
        assertNull(tokenService.findClientByToken("test"));
    }

    @Test
    void testFindClientByTokenWhenTokenIsExpiredShouldReturnNull() {
        ClientAuthToken authToken = mock(ClientAuthToken.class);

        when(authToken.getExpirationDate())
                .thenReturn(LocalDateTime.now().minusMinutes(1));

        when(tokenRepository.findById("test"))
                .thenReturn(Optional.of(authToken));

        assertNull(tokenService.findClientByToken("test"));
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
