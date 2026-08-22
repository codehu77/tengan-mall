package com.tengan.mall.seckill.application.activity;

import com.tengan.mall.seckill.domain.model.SeckillSku;
import com.tengan.mall.seckill.domain.repository.SeckillActivityRepository;
import com.tengan.mall.seckill.domain.repository.SeckillSkuRepository;
import com.tengan.mall.seckill.infrastructure.redis.QuotaGuardAdapter;
import com.tengan.mall.seckill.infrastructure.redis.SeckillCacheAdapter;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 不擋任何狀態的刪除（後台自行負責，比照 DeleteSessionService 同樣的判斷）。
 *
 * <p>刪除前要先清掉 Redis（`seckill:sku:*` 快取 + `seckill:stock:*` 配額鎖）——原本設計是「不管，
 * 讓它自然過期」，但實際測試發現這個假設有漏洞：`ConfirmOrderService`/`CreateOrderService` 判斷
 * 購物車項目要不要套秒殺價，讀的是 Redis（{@code CheckActiveSkusService}），不是 DB；活動被刪除後
 * DB 已經查不到，但 Redis key 還在，導致確認訂單頁面仍然誤顯示秒殺價（雖然真的送出訂單時
 * {@code ReserveQuotaService} 會因為查不到 DB 活動而擋下來，不會真的用錯誤價格成交，但確認頁顯示
 * 錯誤價格本身就是誤導使用者的真實 bug，見使用者實測回報）。</p>
 */
@Service
public class DeleteActivityService implements DeleteActivityUseCase {

    private final SeckillActivityRepository activityRepository;
    private final SeckillSkuRepository skuRepository;
    private final SeckillCacheAdapter cacheAdapter;
    private final QuotaGuardAdapter quotaGuardAdapter;

    public DeleteActivityService(SeckillActivityRepository activityRepository, SeckillSkuRepository skuRepository,
            SeckillCacheAdapter cacheAdapter, QuotaGuardAdapter quotaGuardAdapter) {
        this.activityRepository = activityRepository;
        this.skuRepository = skuRepository;
        this.cacheAdapter = cacheAdapter;
        this.quotaGuardAdapter = quotaGuardAdapter;
    }

    @Override
    public void delete(Long id) {
        List<SeckillSku> skus = skuRepository.findByActivityId(id);
        for (SeckillSku sku : skus) {
            cacheAdapter.evict(sku.getSkuId());
            quotaGuardAdapter.clear(sku.getSkuId());
        }

        skuRepository.replaceForActivity(id, List.of());
        activityRepository.delete(id);
    }
}
