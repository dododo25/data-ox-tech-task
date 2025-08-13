package com.dododo.dataox.client.controller;

import com.dododo.dataox.client.dao.ClientLoginDAO;
import com.dododo.dataox.client.service.ClientTokenService;
import com.dododo.dataox.core.dao.ErrorMessage;
import com.dododo.dataox.client.dao.NewClientDAO;
import com.dododo.dataox.core.exception.UnsuccessfulLoginException;
import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.core.repository.ClientRepository;
import com.dododo.dataox.core.service.HashService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
public class AuthController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private HashService hashService;

    @Autowired
    private ClientTokenService tokenService;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Client.class))}),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PostMapping("/register")
    public ResponseEntity<Client> register(@RequestBody NewClientDAO dao, HttpServletResponse response) {
        Optional<Client> clientOptional = clientRepository.findByEmail(dao.getEmail());

        if (clientOptional.isPresent()) {
            throw new IllegalArgumentException(String.format("client with email %s already exists", dao.getEmail()));
        }

        String passwordHash = hashService.prepareHash(dao.getPassword());

        Client saved = clientRepository.save(Client.builder()
                .name(dao.getName())
                .email(dao.getEmail())
                .passwordHash(passwordHash)
                .address(dao.getAddress())
                .active(true)
                .lastActivityTimestamp(LocalDateTime.now())
                .build());

        response.setHeader("Auth-Token", tokenService.createFor(saved));

        return new ResponseEntity<>(saved, CREATED);
    }

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
    public Client login(@RequestBody ClientLoginDAO dao, HttpServletResponse response) {
        Client client = clientRepository.findByEmail(dao.getEmail())
                .orElseThrow(() -> new UnsuccessfulLoginException("unknown user's email"));

        String passwordHash = hashService.prepareHash(dao.getPassword());

        if (!Objects.equals(passwordHash, client.getPasswordHash())) {
            throw new UnsuccessfulLoginException("wrong password");
        }

        client.setLastActivityTimestamp(LocalDateTime.now());

        clientRepository.save(client);
        response.setHeader("Auth-Token", tokenService.createFor(client));

        return client;
    }
}
