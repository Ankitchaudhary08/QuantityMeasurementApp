package com.app.quantitymeasurement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserEntity — Stores Google OAuth2 authenticated user details.
 * Created/updated on every successful Google login.
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "app_user",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email", unique = true),
                @Index(name = "idx_user_google_id", columnList = "googleId", unique = true)
        })
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Google subject ID (unique per Google account) */
    @Column(nullable = false, unique = true)
    private String googleId;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    /** Profile picture URL from Google */
    private String pictureUrl;

    /** Role assigned to this user */
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
