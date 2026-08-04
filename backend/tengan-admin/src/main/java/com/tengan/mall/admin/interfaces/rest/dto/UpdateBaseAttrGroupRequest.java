package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBaseAttrGroupRequest(@NotBlank String name, int sort) {
}
