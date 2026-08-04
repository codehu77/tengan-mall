package com.tengan.mall.admin.interfaces.rest.dto;

import java.util.List;

public record CategoryTreeItemResponse(Long id, String name, String icon, int sort, int status,
        List<CategoryTreeItemResponse> children) {
}
