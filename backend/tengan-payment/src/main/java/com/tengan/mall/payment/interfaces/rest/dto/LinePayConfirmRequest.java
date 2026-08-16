package com.tengan.mall.payment.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record LinePayConfirmRequest(@NotBlank String transactionId) {
}
