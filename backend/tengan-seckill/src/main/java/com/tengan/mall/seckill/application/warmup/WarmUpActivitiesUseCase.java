package com.tengan.mall.seckill.application.warmup;

public interface WarmUpActivitiesUseCase {

    /** 回傳這次處理的活動數量，供排程 log/立即觸發端點回顯使用。 */
    int warmUp();
}
