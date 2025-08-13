package com.dododo.dataox.client.service;

import com.dododo.dataox.client.controller.OrderController;
import com.dododo.dataox.client.dao.OrderDAO;
import com.dododo.dataox.client.exception.ClientInsufficientFundsException;
import com.dododo.dataox.core.exception.ControllerException;
import com.dododo.dataox.client.exception.TooManyRequestsException;
import com.dododo.dataox.core.exception.UnsuccessfulLoginException;
import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.core.model.Order;
import com.dododo.dataox.core.repository.ClientRepository;
import com.dododo.dataox.core.repository.OrderRepository;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Service
public class OrderService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ClientRepository clientRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private final Map<Long, Bucket> cache;

    private final ScheduledExecutorService executor;

    public OrderService() {
        this.cache = new ConcurrentHashMap<>();
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }

    public List<Order> findAll() {
        return orderRepository.findAllByClosedAtIsNull();
    }

    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    public Optional<Order> findByNameAndSupplierAndConsumer(String name, Long supplierId, Long consumerId) {
        Client supplier = clientRepository.findById(supplierId)
                .orElseThrow(() -> new UnsuccessfulLoginException("unknown client"));
        Client consumer = clientRepository.findById(consumerId)
                .orElseThrow(() -> new UnsuccessfulLoginException("unknown client"));

        return orderRepository.findByNameAndSupplierAndConsumer(name, supplier, consumer);
    }

    public Order createNewOrder(OrderDAO newOrder, Client supplier)
            throws ExecutionException, InterruptedException {
        if (!cache.containsKey(supplier.getId())) {
            cache.put(supplier.getId(), createNewBucket());
        }

        Bucket bucket = cache.get(supplier.getId());

        if (!bucket.tryConsume(1)) {
            throw new TooManyRequestsException();
        }

        long now = System.currentTimeMillis();

        return executor.schedule(() -> {
            if (newOrder.getPrice() <= 0) {
                throw new IllegalArgumentException("price value must be greater than zero");
            }

            LocalDateTime createdAt = LocalDateTime.ofInstant(Instant.ofEpochMilli(now), ZoneOffset.UTC);

            Order saved = orderRepository.save(Order.builder()
                    .name(String.format("Order #%d", getNextSequenceValue()))
                    .supplier(supplier)
                    .price(newOrder.getPrice())
                    .createdAt(createdAt)
                    .dbWriteLatency(System.currentTimeMillis() - now)
                    .build());

            LOGGER.info("create new order - {}", saved);

            return saved;
        }, (long) (Math.random() * 9 + 1), TimeUnit.SECONDS).get();
    }

    public Order closeOrder(Long id, Client consumer) {
        // find the relative order
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new NullPointerException(String.format("no order by id %s", id)));

        if (order.getClosedAt() != null) {
            throw new ControllerException(BAD_REQUEST, "this order was already closed");
        }

        Client supplier = order.getSupplier();

        // if after the client pays the price of the order its profit is less that -1000, throw exception
        if (order.getPrice() - consumer.getProfit() > 1000) {
            throw new ClientInsufficientFundsException(consumer.getId());
        }

        LocalDateTime closedAt = LocalDateTime.now();

        // change client profit values
        consumer.setProfit(consumer.getProfit() - order.getPrice());
        supplier.setProfit(supplier.getProfit() + order.getPrice());

        order.setConsumer(consumer);
        order.setClosedAt(closedAt);

        clientRepository.save(consumer);
        clientRepository.save(supplier);

        Order closedOrder = orderRepository.save(order);

        LOGGER.info("{} was closed", order.getName());

        return closedOrder;
    }

    private static Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.classic(1, Refill.greedy(1, Duration.ofSeconds(10)));

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    private Long getNextSequenceValue() {
        return (Long) entityManager.createNativeQuery("SELECT last_value FROM order_data_id_seq")
                .getSingleResult();
    }
}
