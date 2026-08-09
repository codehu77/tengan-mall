package com.tengan.mall.admin.application.port;

import java.util.List;

public record GrantCouponsPayload(Long templateId, List<Long> userIds) {
}
