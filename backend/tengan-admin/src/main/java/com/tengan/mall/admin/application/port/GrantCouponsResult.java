package com.tengan.mall.admin.application.port;

import java.util.List;

public record GrantCouponsResult(List<Long> succeededUserIds, List<Long> skippedUserIds) {
}
