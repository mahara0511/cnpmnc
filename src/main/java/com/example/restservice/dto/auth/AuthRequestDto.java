package com.example.restservice.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Authentication request containing user credentials")
public class AuthRequestDto {
    
    @Schema(description = "User email address", example = "user@example.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @Schema(description = "User password", example = "password123", required = true)
    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be between 6 and 100 characters")
    private String password;
}