package com.app.quantitymeasurement.repository;

import com.app.quantitymeasurement.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * UserRepository — JPA repository for UserEntity.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByGoogleId(String googleId);

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
