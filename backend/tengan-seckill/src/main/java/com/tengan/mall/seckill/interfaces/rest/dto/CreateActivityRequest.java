package com.tengan.mall.seckill.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

/**
 * activityType 目前恆為 "FLASH_SALE"，保留這個欄位只是為了不用同時改 tengan-admin 那邊的
 * pass-through DTO——controller 不再讀取/驗證它的值。
 */
public record CreateActivityRequest(@NotBlank String activityType, Long sessionId, LocalDate activityDate) {
}
