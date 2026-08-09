package com.tengan.mall.coupon.application.membercoupon;

import java.util.List;

public record GrantCouponsCommand(String operator, Long templateId, List<Long> userIds) {
}
