package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 不擋任何狀態的刪除（後台自行負責，比照 DeleteSessionService 同樣的判斷）。刪除 ACTIVE 活動時
 * Redis 裡的配額 key 不會被動到，會照原本 TTL 自然過期；期間若有人剛好在下單保留配額，
 * {@code ReserveQuotaService} 查不到 DB 活動會丟 {@code ActivityNotFoundException}（404），
 * `tengan-order` 既有的錯誤處理會統一翻譯成「配額不足或活動已結束」，行為上是合理的降級，
 * 不需要額外處理。
 */
@Service
public class DeleteActivityService implements DeleteActivityUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;

    public DeleteActivityService(SeckillActivityRepository activityRepository, SeckillSkuRepository skuRepository) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
    }

    @Override
    public void delete(Long id) {
        skuRepository.replaceForActivity(id, List.of());
        activityRepository.delete(id);
    }
}
