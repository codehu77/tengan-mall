package com.tengan.mall.payment.application.subscription;

public record SubscribeCommand(Long memberId, String targetTier) {
}
