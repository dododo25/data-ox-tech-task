package com.dododo.dataox.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_data")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(targetEntity = Client.class)
    private Client supplier;

    @ManyToOne(targetEntity = Client.class)
    private Client consumer;

    private double price;

    private LocalDateTime createdAt;

    private LocalDateTime closedAt;

    private long dbWriteLatency;

}
