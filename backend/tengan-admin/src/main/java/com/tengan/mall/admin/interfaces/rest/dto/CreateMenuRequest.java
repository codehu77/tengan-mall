package com.tengan.mall.admin.interfaces.rest.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreateMenuRequest(Long parentId, @Min(1) @Max(3) int menuType, @NotBlank String title, String path,
        String component, String routeName, String icon, String permissionCode, int sortOrder) {
}
