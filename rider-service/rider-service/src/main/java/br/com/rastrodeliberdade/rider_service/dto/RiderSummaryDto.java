package br.com.rastrodeliberdade.rider_service.dto;

import java.util.UUID;

public record RiderSummaryDto(
        UUID id,
        String bikerNickname,
        String email,
        String city,
        String state

) {
}
