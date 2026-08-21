package com.tengan.mall.seckill.application.reservation;

public record ReleaseQuotaCommand(Long skuId, Long memberId, int count) {
}
