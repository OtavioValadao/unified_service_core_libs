package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record ServiceOrderDto(
        String orderNumber,
        ClientDto client,
        VehicleDto vehicleDto,
        String completionDate
) {
}
