package br.com.rastrodeliberdade.rider_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RiderInsertDto(
        @NotBlank
        String fullName,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String bikerNickname,
        @NotBlank
        @Size(min=8, message="A senha deve ter no minimo 8 caracteres")
        String password,
        @NotBlank
        String city,
        @NotBlank
        String state
        
) {
}
