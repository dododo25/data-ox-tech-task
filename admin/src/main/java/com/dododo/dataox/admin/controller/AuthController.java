package com.dododo.dataox.admin.controller;

import com.dododo.dataox.admin.dao.AdminDAO;
import com.dododo.dataox.admin.service.AdminTokenService;
import com.dododo.dataox.core.dao.ErrorMessage;
import com.dododo.dataox.core.exception.UnsuccessfulLoginException;
import com.dododo.dataox.core.model.Admin;
import com.dododo.dataox.core.repository.AdminRepository;
import com.dododo.dataox.core.service.HashService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;

@RestController
public class AuthController {

    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private HashService hashService;

    @Autowired
    private AdminTokenService tokenService;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PostMapping("/login")
    public void login(@RequestBody AdminDAO dao, HttpServletResponse response) {
        Admin admin = adminRepository.findByName(dao.getName())
                .orElseThrow(() -> new UnsuccessfulLoginException("unknown user's email"));

        String passwordHash = hashService.prepareHash(dao.getPassword());

        if (!Objects.equals(passwordHash, admin.getPasswordHash())) {
            throw new UnsuccessfulLoginException("wrong password");
        }

        admin.setLastActivityTimestamp(LocalDateTime.now());
        adminRepository.save(admin);

        response.setHeader("Auth-Token", tokenService.createFor(admin));
    }
}
