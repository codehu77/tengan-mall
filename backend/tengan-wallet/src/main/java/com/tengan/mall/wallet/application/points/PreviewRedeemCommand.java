package com.tengan.mall.wallet.application.points;

import java.math.BigDecimal;

public record PreviewRedeemCommand(Long memberId, BigDecimal orderAmount, int points) {
}
