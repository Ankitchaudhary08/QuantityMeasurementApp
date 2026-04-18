package com.app.userservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserEntity — JPA entity for user accounts.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "app_user",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email", unique = true)
        })
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    /** BCrypt-encoded password */
    @Column(nullable = false)
    private String password;

    /** Profile picture URL (optional) */
    private String pictureUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime lastLoginAt;

    @PrePersist
    void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.lastLoginAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        this.lastLoginAt = LocalDateTime.now();
    }

    public enum Role {
        USER, ADMIN
    }
}
