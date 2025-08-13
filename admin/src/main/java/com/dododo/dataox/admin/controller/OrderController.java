package com.dododo.dataox.admin.controller;

import com.dododo.dataox.core.dao.ErrorMessage;
import com.dododo.dataox.core.exception.ControllerException;
import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.core.model.Order;
import com.dododo.dataox.core.repository.OrderRepository;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Client.class))))
    })
    @GetMapping("/list")
    public List<Order> getAll() {
        return orderRepository.findAll();
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
    public Order getById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .orElseThrow(NullPointerException::new);
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
    public void close(@PathVariable Long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new ControllerException(
                HttpStatus.BAD_REQUEST,
                String.format("unknown client with id %d", id)));

        if (order.getClosedAt() != null) {
            return;
        }

        order.setClosedAt(LocalDateTime.now());

        orderRepository.save(order);
    }

    @ApiResponses({
            @ApiResponse(responseCode = "204")
    })
    @DeleteMapping("/{id}/delete")
    public void delete(@PathVariable Long id) {
        orderRepository.deleteById(id);
    }
}
