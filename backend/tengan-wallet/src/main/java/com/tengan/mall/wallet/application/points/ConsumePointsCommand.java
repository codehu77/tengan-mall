package com.tengan.mall.wallet.application.points;

public record ConsumePointsCommand(Long memberId, int points, String orderSn) {
}
