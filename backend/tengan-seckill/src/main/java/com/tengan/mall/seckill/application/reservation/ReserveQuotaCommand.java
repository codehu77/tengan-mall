package com.tengan.mall.seckill.application.reservation;

public record ReserveQuotaCommand(Long skuId, Long memberId, int count) {
}
