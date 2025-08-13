package com.dododo.dataox.core.repository;

import com.dododo.dataox.core.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {

    Optional<Admin> findByName(String name);

}
