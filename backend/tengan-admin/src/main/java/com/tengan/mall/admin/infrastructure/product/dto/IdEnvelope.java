package com.tengan.mall.admin.infrastructure.product.dto;

/** tengan-product 的 Create* 端點統一回傳 {"id": ...}，category/brand 共用同一個反序列化形狀。 */
public record IdEnvelope(Long id) {
}
