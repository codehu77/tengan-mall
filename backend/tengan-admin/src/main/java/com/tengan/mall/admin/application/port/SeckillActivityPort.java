package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-seckill 的活動 internal 端點，跟 {@link CouponTemplatePort} 同樣的純代理原則。 */
public interface SeckillActivityPort {

    List<SeckillActivityItem> listActivities();

    SeckillActivityDetail getActivity(Long id);

    Long createActivity(CreateSeckillActivityPayload payload);

    void deleteActivity(Long id);

    void updateActivitySkus(Long id, UpdateSeckillActivitySkusPayload payload);

    /** 「設定活動商品」列表頁的新增/編輯/刪除單一商品用，只覆蓋這個商品範圍，其餘商品不受影響。 */
    void replaceProductSkus(Long activityId, Long spuId, ReplaceProductSkusPayload payload);

    /** 立即預熱：不用等排程固定的四個時間點，demo/測試新建場次時用（見 tengan-seckill InternalSeckillController 說明）。 */
    int triggerWarmUpNow();
}
