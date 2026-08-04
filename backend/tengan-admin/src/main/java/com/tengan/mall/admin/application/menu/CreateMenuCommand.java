package com.tengan.mall.admin.application.menu;

public record CreateMenuCommand(Long operatorId, String operatorUsername, Long parentId, int menuType, String title,
        String path, String component, String routeName, String icon, String permissionCode, int sortOrder) {
}
