package com.tengan.mall.admin.application.operlog;

import java.time.Instant;

public record OperLogItem(Long id, Long adminUserId, String username, String module, String action,
        String targetDesc, int resultStatus, Instant createdAt) {
}
