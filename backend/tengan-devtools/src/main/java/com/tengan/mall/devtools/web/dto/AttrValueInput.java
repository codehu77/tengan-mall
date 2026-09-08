package com.tengan.mall.devtools.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AttrValueInput(@NotNull Long attrId, @NotBlank String value) {
}
