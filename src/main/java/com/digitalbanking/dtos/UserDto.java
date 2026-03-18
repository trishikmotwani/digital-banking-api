package com.digitalbanking.dtos;

import lombok.*;
import java.time.LocalDateTime;

import com.digitalbanking.entities.UserRole;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String username;
    private UserRole role;
    private boolean isActive;
    private LocalDateTime lastLoggedIn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

