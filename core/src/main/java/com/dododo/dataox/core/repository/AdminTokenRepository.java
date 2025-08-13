package com.dododo.dataox.core.repository;

import com.dododo.dataox.core.model.AdminAuthToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminTokenRepository extends JpaRepository<AdminAuthToken, String> {

}
