package com.tengan.mall.inventory.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

/** release/deduct 共用同一種 request 形狀，比照文件 API 清單兩者的 body 都是 {orderSn}。 */
public record OrderSnRequest(@NotBlank String orderSn) {
}
