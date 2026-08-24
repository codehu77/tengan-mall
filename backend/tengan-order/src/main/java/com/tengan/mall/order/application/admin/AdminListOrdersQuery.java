package com.tengan.mall.order.application.admin;

import java.time.Instant;

public record AdminListOrdersQuery(Integer status, Instant createdFrom, Instant createdTo, int pageNum,
        int pageSize) {
}
