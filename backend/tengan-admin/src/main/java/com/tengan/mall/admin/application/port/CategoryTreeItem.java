package com.tengan.mall.admin.application.port;

import java.util.List;

public record CategoryTreeItem(Long id, String name, String icon, int sort, int status,
        List<CategoryTreeItem> children) {
}
