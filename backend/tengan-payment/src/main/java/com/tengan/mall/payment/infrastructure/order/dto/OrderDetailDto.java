package com.tengan.mall.payment.infrastructure.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.math.BigDecimal;
import java.util.List;

/** 只取 tengan-order GET /internal/orders/{orderSn}（OrderDetailResponse）用得到的欄位子集。 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderDetailDto(String orderSn, Long memberId, int status, BigDecimal payAmount,
        List<OrderItemDto> items) {

    public record OrderItemDto(Long skuId, String skuName) {
    }
}
