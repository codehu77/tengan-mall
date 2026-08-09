package com.tengan.mall.admin.infrastructure.coupon.dto;

import java.util.List;

public record GrantEnvelope(List<Long> succeededUserIds, List<Long> skippedUserIds) {
}
