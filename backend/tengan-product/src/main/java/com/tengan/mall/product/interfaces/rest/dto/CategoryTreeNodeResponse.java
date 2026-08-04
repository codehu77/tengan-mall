package com.tengan.mall.product.interfaces.rest.dto;

import java.util.List;

public record CategoryTreeNodeResponse(Long id, String name, String icon, int sort, int status,
        List<CategoryTreeNodeResponse> children) {
}
