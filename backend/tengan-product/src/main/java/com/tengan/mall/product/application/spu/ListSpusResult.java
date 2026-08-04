package com.tengan.mall.product.application.spu;

import java.util.List;

public record ListSpusResult(List<SpuSummary> items, long total) {
}
