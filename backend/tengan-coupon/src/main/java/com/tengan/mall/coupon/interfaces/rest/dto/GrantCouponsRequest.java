package com.tengan.mall.coupon.interfaces.rest.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record GrantCouponsRequest(@NotNull Long templateId, @NotEmpty List<Long> userIds) {
}
