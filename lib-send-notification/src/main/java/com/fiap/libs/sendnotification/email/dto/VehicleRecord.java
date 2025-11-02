package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record VehicleRecord(
        String plate,
        ModelRecord model
) {
}
