package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateBrandRequest(@NotBlank String name, String logo, String descript, String firstLetter, int sort) {
}
