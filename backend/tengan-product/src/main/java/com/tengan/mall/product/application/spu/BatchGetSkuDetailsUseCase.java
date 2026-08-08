package com.tengan.mall.product.application.spu;

import java.util.List;

public interface BatchGetSkuDetailsUseCase {

    List<SkuDetailView> get(List<Long> skuIds);
}
