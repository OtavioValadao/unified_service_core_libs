package com.fiap.libs.sendnotification.email.dto;

import lombok.Builder;

@Builder
public record ServiceOrderApprovalRecord(
        String orderNumber,
        CustomerRecord client,
        String vehicleInfo,
        String approvalUrl,
        String rejectionUrl
) {
}

