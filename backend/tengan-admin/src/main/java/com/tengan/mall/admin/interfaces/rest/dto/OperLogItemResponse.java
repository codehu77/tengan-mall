package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.Instant;

public record OperLogItemResponse(Long id, Long adminUserId, String username, String module, String action,
        String targetDesc, int resultStatus, Instant createdAt) {
}
