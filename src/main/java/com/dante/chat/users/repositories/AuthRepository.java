package com.dante.chat.users.repositories;

import com.dante.chat.users.model.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthRepository extends JpaRepository<AuthEntity, Long> {
    Optional<AuthEntity> findByToken(String token);
}
