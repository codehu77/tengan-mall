package com.tengan.mall.seckill.application.settlement;

public interface SettleActivitiesUseCase {

    /** 回傳這次真正轉為 SETTLED 的活動數量，供排程 log/立即觸發端點回顯使用。 */
    int settle();
}
