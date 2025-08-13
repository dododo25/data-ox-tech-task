package com.dododo.dataox.core.repository;

import com.dododo.dataox.core.model.Client;
import com.dododo.dataox.core.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllBySupplierAndClosedAtIsNull(Client supplier);

    List<Order> findAllBySupplier(Client consumer);

    List<Order> findAllByConsumer(Client consumer);

    List<Order> findAllByClosedAtIsNull();

    Optional<Order> findByNameAndSupplierAndConsumer(String name, Client supplier, Client consumer);

}
