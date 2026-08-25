package com.tengan.mall.product.application.spu;

import java.util.List;

public interface RecordOrderCompletedSaleCountUseCase {

    void record(String orderSn, List<SkuCountItem> items);
}
