package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record CustomerRecord(
        String nickName,
        String email
) {

}
