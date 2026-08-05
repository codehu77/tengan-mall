package com.tengan.mall.product.interfaces.rest.dto;

import java.util.List;

public record SearchExportResponse(List<SkuSearchDocumentResponse> skus, boolean hasNext) {
}
