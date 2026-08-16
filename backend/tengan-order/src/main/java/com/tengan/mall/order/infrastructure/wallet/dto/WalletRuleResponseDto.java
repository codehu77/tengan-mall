package com.tengan.mall.order.infrastructure.wallet.dto;

/** 只取排程算截止時間用得到的欄位，其餘欄位關掉 FAIL_ON_UNKNOWN_PROPERTIES 忽略。 */
public record WalletRuleResponseDto(int gracePeriodMinutes) {
}
