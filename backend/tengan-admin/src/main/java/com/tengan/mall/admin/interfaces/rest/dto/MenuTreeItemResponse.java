package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record MenuTreeItemResponse(Long id, Long parentId, int menuType, String title, String path,
        String component, String routeName, String icon, String permissionCode, int sortOrder, int status,
        List<MenuTreeItemResponse> children) {
}
