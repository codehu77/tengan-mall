package com.tengan.mall.admin.application.menu;

public record UpdateMenuCommand(Long operatorId, String operatorUsername, Long id, String title, String path,
        String component, String routeName, String icon, String permissionCode, int sortOrder) {
}
