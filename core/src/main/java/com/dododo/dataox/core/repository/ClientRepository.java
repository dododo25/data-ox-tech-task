package com.dododo.dataox.core.repository;

import com.dododo.dataox.core.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<Client> findByName(String name);

    Optional<Client> findByEmail(String email);
}
