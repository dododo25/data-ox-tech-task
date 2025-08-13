package com.dododo.dataox.client.service;

import com.dododo.dataox.core.model.ClientAuthToken;
import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.core.repository.ClientTokenRepository;
import com.dododo.dataox.core.service.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ClientTokenService {

    @Autowired
    private ClientTokenRepository tokenRepository;

    @Autowired
    private HashService hashService;

    private long counter;

    public Client findClientByToken(String token) {
        ClientAuthToken authToken = tokenRepository.findById(token)
                .orElse(null);

        if (authToken == null || authToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            return null;
        }

        return authToken.getClient();
    }

    public String createFor(Client client) {
        String generated = hashService.prepareNoSaltHash(String.valueOf(System.currentTimeMillis() + counter++));

        tokenRepository.save(ClientAuthToken.builder()
                .token(generated)
                .client(client)
                .expirationDate(LocalDateTime.now().plusDays(1))
                .build());

        return generated;
    }
}
