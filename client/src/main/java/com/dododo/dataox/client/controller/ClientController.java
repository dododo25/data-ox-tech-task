package com.dododo.dataox.client.controller;

import com.dododo.dataox.client.dao.EditClientDAO;
import com.dododo.dataox.core.dao.ErrorMessage;
import com.dododo.dataox.core.exception.ControllerException;
import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.core.model.Order;
import com.dododo.dataox.core.repository.ClientRepository;
import com.dododo.dataox.core.repository.OrderRepository;
import com.dododo.dataox.core.service.HashService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
@RequestMapping("/client")
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private HashService hashService;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(
                    responseCode = "401",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/{id}/order/list")
    public List<Order> getAllClientOrders(@PathVariable("id") Long id, HttpServletRequest request) {
        Client client = (Client) request.getAttribute("authClient");

        if (client == null) {
            throw new NullPointerException();
        }

        if (!Objects.equals(client.getId(), id)) {
            throw new ControllerException(HttpStatus.UNAUTHORIZED, "you unauthorized to access this info");
        }

        return Stream.concat(
                        orderRepository.findAllByConsumer(client).stream(),
                        orderRepository.findAllBySupplier(client).stream())
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .toList();
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Order.class)))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/{id}/order/open/list")
    public List<Order> getAllOpenClientOrders(@PathVariable("id") Long clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(NullPointerException::new);

        return orderRepository.findAllBySupplierAndClosedAtIsNull(client);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Client.class)))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(
                    responseCode = "401",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PostMapping("/{id}/edit")
    // currently can't use PatchMapping due to Java language restrictions. Should be resolved later
    public Client edit(@PathVariable("id") Long id, @RequestBody EditClientDAO dao, HttpServletRequest request) {
        Client client = (Client) request.getAttribute("authClient");

        if (client == null) {
            throw new NullPointerException();
        }

        if (!Objects.equals(client.getId(), id)) {
            throw new ControllerException(HttpStatus.UNAUTHORIZED, "you unauthorized to access this info");
        }

        String passwordHash = Optional.ofNullable(dao.getPassword())
                .map(v -> hashService.prepareHash(v))
                .orElse(client.getPasswordHash());

        client.setName(Optional.ofNullable(dao.getName()).orElse(client.getName()));
        client.setEmail(Optional.ofNullable(dao.getEmail()).orElse(client.getEmail()));
        client.setPasswordHash(passwordHash);
        client.setAddress(Optional.ofNullable(dao.getAddress()).orElse(client.getAddress()));

        return clientRepository.save(client);
    }
}
