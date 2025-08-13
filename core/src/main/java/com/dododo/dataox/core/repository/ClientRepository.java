package com.dododo.dataox.core.repository;

import com.dododo.dataox.core.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    List<Client> findAllByProfitBetween(double fromProfit, double toProfit);

    Optional<Client> findByEmail(String email);
}
