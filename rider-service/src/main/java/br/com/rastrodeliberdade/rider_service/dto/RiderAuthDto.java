package br.com.rastrodeliberdade.rider_service.dto;

import java.util.UUID;

public record RiderAuthDto(
        UUID id,
        String email,
        String password
) {
}
