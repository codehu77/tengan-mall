package com.tengan.mall.seckill.application.activity;

import java.util.List;

public interface RemoveProductSkusUseCase {

    /** tengan-product 商品規格真的被刪除時呼叫——清掉這些 skuId 在秒殺這邊的所有殘留參照。 */
    void remove(List<Long> skuIds);
}
