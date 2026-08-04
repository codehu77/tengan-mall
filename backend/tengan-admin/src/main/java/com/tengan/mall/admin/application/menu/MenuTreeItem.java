package com.tengan.mall.admin.application.menu;

import java.util.List;

/** 管理視角的完整選單樹（不做權限篩選，跟 menutree 套件的「可見路由樹」是兩個不同用途）。 */
public record MenuTreeItem(Long id, Long parentId, int menuType, String title, String path, String component,
        String routeName, String icon, String permissionCode, int sortOrder, int status,
        List<MenuTreeItem> children) {
}
