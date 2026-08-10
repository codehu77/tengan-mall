package com.tengan.mall.order.application.order;

import jakarta.validation.constraints.NotBlank;

/**
 * 收件資訊快照——前端結帳頁自己呼叫 GET /api/customer/member/addresses 選好地址後，把完整值物件
 * 送進來，不是送 addressId。tengan-order 完全不需要呼叫 tengan-member（見規劃文件二、）。
 */
public record ReceiverInfo(@NotBlank String receiverName, @NotBlank String receiverPhone, @NotBlank String city,
        @NotBlank String district, String postalCode, @NotBlank String street) {
}
