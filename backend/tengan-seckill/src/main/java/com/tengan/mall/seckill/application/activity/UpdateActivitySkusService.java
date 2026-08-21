package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.exception.ActivityNotFoundException;
import com.tengan.mall.seckill.domain.model.ActivityStatus;
import com.tengan.mall.seckill.domain.model.SeckillActivity;
import com.tengan.mall.seckill.domain.model.SeckillSku;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import org.springframework.stereotype.Service;

/**
 * 設定商品清單是後台唯一的「完成編輯」動作（規劃裡沒有另外設計 publish 按鈕），所以這裡順便把
 * DRAFT 活動轉成 PUBLISHED——沒有商品清單的活動本來就不該被預熱排程撈到。已經是 PUBLISHED
 * 以後的活動（含 ACTIVE/SETTLED）呼叫這支不會再變更狀態，只覆蓋商品清單。
 */
@Service
public class UpdateActivitySkusService implements UpdateActivitySkusUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;

    public UpdateActivitySkusService(SeckillActivityRepository activityRepository,
            SeckillSkuRepository skuRepository) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
    }

    @Override
    public void update(UpdateActivitySkusCommand command) {
        SeckillActivity activity = activityRepository.findById(command.activityId())
                .orElseThrow(() -> new ActivityNotFoundException(command.activityId()));

        var skus = command.items().stream()
                .map(item -> SeckillSku.create(activity.getId(), item.skuId(), item.seckillPrice(),
                        item.seckillCount(), item.limitPerUser()))
                .toList();
        skuRepository.replaceForActivity(activity.getId(), skus);

        if (activity.getStatus() == ActivityStatus.DRAFT) {
            activity.publish();
            activityRepository.update(activity);
        }
    }
}
