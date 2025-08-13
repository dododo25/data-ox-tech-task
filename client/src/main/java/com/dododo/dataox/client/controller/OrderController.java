package com.dododo.dataox.client.controller;

import com.dododo.dataox.core.dao.ErrorMessage;
import com.dododo.dataox.client.dao.OrderDAO;
import com.dododo.dataox.core.exception.ControllerException;
import com.dododo.dataox.client.exception.TooManyRequestsException;
import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.core.model.Order;
import com.dododo.dataox.client.service.OrderService;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Order.class))))
    })
    @GetMapping("/open/list")
    public List<Order> getAll() {
        return orderService.findAll();
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping("/{id}/get")
    public Order getById(@PathVariable Long id, HttpServletRequest request) {
        Client client = (Client) request.getAttribute("authClient");

        if (client == null) {
            throw new NullPointerException();
        }

        if (!Objects.equals(client.getId(), id)) {
            throw new ControllerException(HttpStatus.UNAUTHORIZED, "you unauthorized to access this info");
        }

        return orderService.findById(id)
                .orElseThrow(() -> new NullPointerException(String.format("no order by id %s", id)));
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @GetMapping(value = "/get", params = {"name", "supplier", "consumer"})
    public Order getByKey(@RequestParam("name") String name,
                          @RequestParam("supplier") Long supplierId,
                          @RequestParam("consumer") Long consumerId) {
        return orderService.findByNameAndSupplierAndConsumer(name, supplierId, consumerId)
                .orElseThrow(NullPointerException::new);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PostMapping("/add")
    public ResponseEntity<Order> createNewOrder(@RequestBody OrderDAO newOrder, HttpServletRequest request)
            throws ExecutionException, InterruptedException {
        Client supplier = (Client) request.getAttribute("authClient");

        if (supplier == null) {
            throw new NullPointerException();
        }

        if (!supplier.isActive()) {
            throw new IllegalArgumentException("supplier client must be active");
        }

        return new ResponseEntity<>(orderService.createNewOrder(newOrder, supplier), CREATED);
    }

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Order.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = {@Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorMessage.class))})
    })
    @PostMapping("/{id}/close")
    @Transactional
    public Order closeOrder(@PathVariable Long id, HttpServletRequest request) {
        // get the authorized client
        Client consumer = (Client) request.getAttribute("authClient");

        if (consumer == null) {
            throw new NullPointerException();
        }

        return orderService.closeOrder(id, consumer);
    }
}
