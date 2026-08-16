package com.tengan.mall.wallet.application.points;

import java.math.BigDecimal;

public record PreviewRedeemResult(boolean valid, BigDecimal discountAmount) {
}
