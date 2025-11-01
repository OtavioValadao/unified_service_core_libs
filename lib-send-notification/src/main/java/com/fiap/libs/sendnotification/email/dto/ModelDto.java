package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record ModelDto(
        Integer year,
        String model,
        String brand
) {
}
