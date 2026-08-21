package com.tengan.mall.seckill.domain.repository;

import com.tengan.mall.seckill.domain.model.SeckillActivity;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface SeckillActivityRepository {

    SeckillActivity save(SeckillActivity activity);

    /** 持久化 status 轉換（publish/activate/settle）等已存在聚合根的欄位變更。 */
    void update(SeckillActivity activity);

    Optional<SeckillActivity> findById(Long id);

    List<SeckillActivity> findAll(int pageNum, int pageSize);

    long countAll();

    /** status=PUBLISHED 且 startTime 落在 [now, horizon] 內——預熱排程的候選（見規劃第 3 節）。 */
    List<SeckillActivity> findReadyToWarmUp(Instant now, Instant horizon);

    /** status=ACTIVE 且 endTime<=cutoff——結算排程的候選（見規劃第 6 節）。 */
    List<SeckillActivity> findActiveEndedBefore(Instant cutoff);

    /** status=ACTIVE，不限時間——公開展示端點用（見規劃文件第 1 節）。 */
    List<SeckillActivity> findActive();
}
