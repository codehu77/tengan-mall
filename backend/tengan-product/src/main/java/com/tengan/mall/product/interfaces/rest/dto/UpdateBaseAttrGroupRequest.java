package com.tengan.mall.product.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBaseAttrGroupRequest(@NotBlank String name, int sort) {
}
