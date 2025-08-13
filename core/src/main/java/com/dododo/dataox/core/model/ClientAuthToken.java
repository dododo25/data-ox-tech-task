package com.dododo.dataox.core.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table
public class ClientAuthToken {

    @Id
    private String token;

    private LocalDateTime expirationDate;

    @OneToOne(targetEntity = Client.class)
    private Client client;

}
