package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateMenuRequest(@NotBlank String title, String path, String component, String routeName,
        String icon, String permissionCode, int sortOrder) {
}
