package com.sanju.auth_service.repository;

import com.sanju.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserManagementRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}

