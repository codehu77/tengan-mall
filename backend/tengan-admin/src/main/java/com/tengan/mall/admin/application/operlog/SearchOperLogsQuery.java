package com.tengan.mall.admin.application.operlog;

import java.time.Instant;

public record SearchOperLogsQuery(Long adminId, String module, Instant from, Instant to, int pageNum,
        int pageSize) {
}
