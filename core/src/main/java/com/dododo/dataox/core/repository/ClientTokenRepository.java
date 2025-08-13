package com.dododo.dataox.core.repository;

import com.dododo.dataox.core.model.ClientAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClientTokenRepository extends JpaRepository<ClientAuthToken, String> {

}
