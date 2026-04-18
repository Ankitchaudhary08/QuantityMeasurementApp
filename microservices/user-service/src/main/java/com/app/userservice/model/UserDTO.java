package com.app.userservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * UserDTO — Data Transfer Object for user API responses.
 * Never exposes the password field.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;
    private String email;
    private String name;
    private String pictureUrl;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;

    public static UserDTO fromEntity(UserEntity entity) {
        UserDTO dto = new UserDTO();
        dto.setId(entity.getId());
        dto.setEmail(entity.getEmail());
        dto.setName(entity.getName());
        dto.setPictureUrl(entity.getPictureUrl());
        dto.setRole(entity.getRole().name());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLastLoginAt(entity.getLastLoginAt());
        return dto;
    }
}
