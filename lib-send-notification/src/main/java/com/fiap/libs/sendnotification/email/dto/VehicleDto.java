package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record VehicleDto(
        String plate,
        ModelDto model
) {
}
