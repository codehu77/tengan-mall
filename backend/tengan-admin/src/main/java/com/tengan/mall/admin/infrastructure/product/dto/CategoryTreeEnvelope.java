package com.tengan.mall.admin.infrastructure.product.dto;

import com.tengan.mall.admin.application.port.CategoryTreeItem;
import java.util.List;

public record CategoryTreeEnvelope(List<CategoryTreeItem> items) {
}
