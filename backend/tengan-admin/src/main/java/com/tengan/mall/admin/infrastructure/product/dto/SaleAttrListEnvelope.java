package com.tengan.mall.admin.infrastructure.product.dto;

import com.tengan.mall.admin.application.port.SaleAttrItem;
import java.util.List;

public record SaleAttrListEnvelope(List<SaleAttrItem> items) {
}
