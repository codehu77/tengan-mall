package com.tengan.mall.product.application.category;

import java.util.List;

public record CategoryTreeNode(Long id, String name, String icon, int sort, int status,
        List<CategoryTreeNode> children) {
}
