package com.tengan.mall.admin.infrastructure.product.dto;

import com.tengan.mall.admin.application.port.BrandItem;
import java.util.List;

public record BrandListEnvelope(List<BrandItem> items) {
}
