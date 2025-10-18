package br.com.rastrodeliberdade.auth_service.dto;

public record LoginRequestDto(
        String email,
        String password) {
}
