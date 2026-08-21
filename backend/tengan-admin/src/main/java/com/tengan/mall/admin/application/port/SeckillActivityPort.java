package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-seckill 的活動 internal 端點，跟 {@link CouponTemplatePort} 同樣的純代理原則。 */
public interface SeckillActivityPort {

    List<SeckillActivityItem> listActivities();

    SeckillActivityDetail getActivity(Long id);

    Long createActivity(CreateSeckillActivityPayload payload);

    void updateActivitySkus(Long id, UpdateSeckillActivitySkusPayload payload);
}
