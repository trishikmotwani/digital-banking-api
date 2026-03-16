package com.digitalbanking.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private String userId;
    private String username;
    private String role;
    private boolean isActive;
    private LocalDateTime lastLoggedIn;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

