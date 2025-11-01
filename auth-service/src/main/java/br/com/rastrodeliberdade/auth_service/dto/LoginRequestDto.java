package br.com.rastrodeliberdade.auth_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequestDto(
        @Email(message = "Invalid email format")
        @NotBlank(message = "Email is required")
        String email,

        @NotBlank(message = "Password is required")
        String password) {
}
