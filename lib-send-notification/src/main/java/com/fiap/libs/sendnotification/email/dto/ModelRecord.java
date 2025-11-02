package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record ModelRecord(
        Integer year,
        String model,
        String brand
) {
}
