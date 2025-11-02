package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record ServiceOrderRecord(
        String orderNumber,
        CustomerRecord client,
        VehicleRecord vehicleRecord,
        String completionDate
) {
}
