package com.tengan.mall.admin.infrastructure.product.dto;

import com.tengan.mall.admin.application.port.SaleAttrStandardValueItem;
import java.util.List;

public record SaleAttrStandardValueListEnvelope(List<SaleAttrStandardValueItem> items) {
}
