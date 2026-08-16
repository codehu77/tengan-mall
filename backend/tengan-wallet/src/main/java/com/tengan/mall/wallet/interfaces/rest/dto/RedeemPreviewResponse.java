package com.tengan.mall.wallet.interfaces.rest.dto;

import java.math.BigDecimal;

public record RedeemPreviewResponse(boolean valid, BigDecimal discountAmount) {
}
