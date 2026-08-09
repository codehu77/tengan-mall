package com.tengan.mall.inventory.application.task;

import java.time.Instant;

public record WareOrderTaskQueryItem(Long id, String orderSn, int status, Instant createdAt) {
}
