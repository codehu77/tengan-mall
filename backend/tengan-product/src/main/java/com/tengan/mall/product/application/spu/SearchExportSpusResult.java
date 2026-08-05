package com.tengan.mall.product.application.spu;

import java.util.List;

public record SearchExportSpusResult(List<SkuSearchDocumentPayload> skus, boolean hasNext) {
}
