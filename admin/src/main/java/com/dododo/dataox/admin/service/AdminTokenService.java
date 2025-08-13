package com.dododo.dataox.admin.service;

import com.dododo.dataox.core.model.Admin;
import com.dododo.dataox.core.model.AdminAuthToken;
import com.dododo.dataox.core.repository.AdminTokenRepository;
import com.dododo.dataox.core.service.HashService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AdminTokenService {

    @Autowired
    private AdminTokenRepository tokenRepository;

    @Autowired
    private HashService hashService;

    private long counter;

    public Admin findAdminByToken(String token) {
        AdminAuthToken authToken = tokenRepository.findById(token)
                .orElse(null);

        if (authToken == null || authToken.getExpirationDate().isBefore(LocalDateTime.now())) {
            return null;
        }

        return authToken.getAdmin();
    }

    public String createFor(Admin admin) {
        String generated = hashService.prepareNoSaltHash(String.valueOf(System.currentTimeMillis() + counter++));

        tokenRepository.save(AdminAuthToken.builder()
                .token(generated)
                .admin(admin)
                .expirationDate(LocalDateTime.now().plusDays(1))
                .build());

        return generated;
    }
}
