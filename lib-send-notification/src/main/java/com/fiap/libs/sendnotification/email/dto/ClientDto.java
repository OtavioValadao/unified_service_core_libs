package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record ClientDto(
        String nickName,
        String email
) {

}
