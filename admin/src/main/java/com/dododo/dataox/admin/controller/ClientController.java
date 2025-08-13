package com.dododo.dataox.admin.controller;

import com.dododo.dataox.admin.dao.EditClientDAO;
import com.dododo.dataox.core.dao.ErrorMessage;
import com.dododo.dataox.admin.dao.OrderSummary;
import com.dododo.dataox.core.exception.ControllerException;
import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.admin.model.ClientRole;
import com.dododo.dataox.core.model.Order;
import com.dododo.dataox.core.repository.ClientRepository;
import com.dododo.dataox.core.repository.OrderRepository;
import com.dododo.dataox.core.service.HashService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.contains;
import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.ignoreCase;

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
                            array = @ArraySchema(schema = @Schema(implementation = Client.class))))
    })
    @GetMapping("/list")
    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Client.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/{id}")
    public Client getById(@PathVariable Long id) {
        return clientRepository.findById(id)
                .orElseThrow(NullPointerException::new);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Client.class))))
    })
    @GetMapping("/find")
    public List<Client> getAllByKey(@RequestParam(required = false) String name,
                                    @RequestParam(required = false) String email,
                                    @RequestParam(required = false) String address) {
        if (name == null && email == null && address == null) {
            return Collections.emptyList();
        }

        Client client = new Client();
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withIgnorePaths("profit", "active");

        if (name != null) {
            client.setName(name);
            matcher = matcher.withMatcher("name", contains());
        }

        if (email != null) {
            client.setEmail(email);
            matcher = matcher.withMatcher("email", contains());
        }

        if (address != null) {
            client.setAddress(address);
            matcher = matcher.withMatcher("address", ignoreCase().contains());
        }

        return clientRepository.findAll(Example.of(client, matcher));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Client.class))))
    })
    @GetMapping("/find/range")
    public List<Client> getAllByRange(@RequestParam double from, @RequestParam double to) {
        return clientRepository.findAllByProfitBetween(from, to);
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
    @GetMapping("/{id}/order/list")
    public List<Order> getAllClientOrders(@PathVariable("id") Long id,
                                          @RequestParam(required = false) ClientRole role) {
        Client client = clientRepository.findById(id)
                .orElseThrow(NullPointerException::new);

        if (role == null) {
            return Stream.concat(
                            orderRepository.findAllBySupplier(client).stream(),
                            orderRepository.findAllByConsumer(client).stream())
                    .toList();
        } else if (role == ClientRole.SELLER) {
            return orderRepository.findAllBySupplier(client);
        } else if (role == ClientRole.BUYER) {
            return orderRepository.findAllByConsumer(client);
        } else {
            throw new ControllerException(HttpStatus.BAD_REQUEST, String.format("unknown role value '%s'", role));
        }
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OrderSummary.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/{id}/order/summary")
    public OrderSummary getAllClientOrdersSummary(@PathVariable("id") Long id) {
        Client client = clientRepository.findById(id)
                .orElseThrow(NullPointerException::new);

        List<Order> supplierOrders = orderRepository.findAllBySupplier(client);
        List<Order> consumerOrders = orderRepository.findAllByConsumer(client);

        double totalIncome = supplierOrders.stream()
                .reduce(0.0, (v1, v2) -> v1 + v2.getPrice(), Double::sum);
        double totalLoss = consumerOrders.stream()
                .reduce(0.0, (v1, v2) -> v1 + v2.getPrice(), Double::sum);

        return OrderSummary.builder()
                .totalSuppliedItems(new OrderSummary.Item(supplierOrders.size(), totalIncome))
                .totalConsumedItems(new OrderSummary.Item(consumerOrders.size(), totalLoss))
                .totalProfit(totalIncome - totalLoss)
                .build();
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
    @PostMapping("/{id}/disable")
    public void disable(@PathVariable Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ControllerException(
                HttpStatus.BAD_REQUEST,
                String.format("unknown client with id %d", id)));

        client.setActive(false);
        client.setLastActivityTimestamp(LocalDateTime.now());

        clientRepository.save(client);
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
    @PostMapping("/{id}/enable")
    public void enable(@PathVariable Long id) {
        Client client = clientRepository.findById(id).orElseThrow(() -> new ControllerException(
                HttpStatus.BAD_REQUEST,
                String.format("unknown client with id %d", id)));

        client.setActive(true);
        client.setLastActivityTimestamp(LocalDateTime.now());

        clientRepository.save(client);
    }

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
                            schema = @Schema(implementation = ErrorMessage.class))}),
            @ApiResponse(
                    responseCode = "401",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PostMapping("/{id}/edit")
    // currently can't use PatchMapping due to Java language restrictions. Should be resolved later
    public Client edit(@PathVariable("id") Long id, @RequestBody EditClientDAO dao) {
        Client client = clientRepository.findById(id)
                .orElseThrow(NullPointerException::new);

        String passwordHash = Optional.ofNullable(dao.getPassword())
                .map(v -> hashService.prepareHash(v))
                .orElse(client.getPasswordHash());

        client.setName(Optional.ofNullable(dao.getName()).orElse(client.getName()));
        client.setEmail(Optional.ofNullable(dao.getEmail()).orElse(client.getEmail()));
        client.setPasswordHash(passwordHash);
        client.setAddress(Optional.ofNullable(dao.getAddress()).orElse(client.getAddress()));
        client.setProfit(Optional.ofNullable(dao.getProfit()).orElse(0.0));

        return clientRepository.save(client);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Client.class)))
    })
    @PostMapping("/reset")
    public List<Client> resetAllClients(@PathVariable Long id) {
        List<Client> clients = clientRepository.findAll();
        clients.forEach(client -> client.setProfit(0));
        return clientRepository.saveAll(clients);
    }
}
