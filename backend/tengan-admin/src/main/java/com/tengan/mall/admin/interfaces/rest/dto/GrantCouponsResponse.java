package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record GrantCouponsResponse(List<Long> succeededUserIds, List<Long> skippedUserIds) {
}
