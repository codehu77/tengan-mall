package com.tengan.mall.devtools.client.dto;

import java.util.List;

/** 對應 tengan-product InternalProductController 的 CategoryTreeNodeResponse。 */
public record CategoryTreeNode(Long id, String name, String icon, int sort, int status,
        List<CategoryTreeNode> children) {
}
