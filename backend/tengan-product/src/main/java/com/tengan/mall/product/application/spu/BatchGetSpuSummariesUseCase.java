package com.tengan.mall.product.application.spu;

import java.util.List;

public interface BatchGetSpuSummariesUseCase {

    List<SpuSummaryView> get(List<Long> spuIds);
}
