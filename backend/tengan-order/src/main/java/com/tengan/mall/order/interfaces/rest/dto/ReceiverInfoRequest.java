package com.tengan.mall.order.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record ReceiverInfoRequest(@NotBlank String receiverName, @NotBlank String receiverPhone,
        @NotBlank String city, @NotBlank String district, String postalCode, @NotBlank String street) {
}
