package com.tengan.mall.inventory.application.sku;

import com.tengan.mall.inventory.domain.repository.MemberSkuPurchaseCountRepository;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import com.tengan.mall.inventory.infrastructure.redis.GateQuotaAdapter;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * tengan-inventory 對 skuId 的「集中清理入口」——這個服務對某個 skuId 擁有什麼，這裡就該清什麼，
 * 不要讓 {@code ProductLaunchConfigChangedListener} 之類的 MQ 監聽器直接分頭呼叫各個 repository。
 * 目前擁有：sku_launch_config（開賣時間/防超賣保護設定）、ware_sku（真實庫存）、
 * member_sku_purchase_count（限購計數，SKU 都不存在了這個限購沒有意義）、GateQuotaAdapter 的
 * Redis 資料（防超賣保護的 semaphore/買家名單，本來就有 TTL 會自動過期，這裡是提前清掉不用等過期）。
 */
@Service
public class RemoveSkuTracesService implements RemoveSkuTracesUseCase {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final WareSkuRepository wareSkuRepository;
    private final MemberSkuPurchaseCountRepository memberSkuPurchaseCountRepository;
    private final GateQuotaAdapter gateQuotaAdapter;

    public RemoveSkuTracesService(SkuLaunchConfigRepository skuLaunchConfigRepository,
            WareSkuRepository wareSkuRepository, MemberSkuPurchaseCountRepository memberSkuPurchaseCountRepository,
            GateQuotaAdapter gateQuotaAdapter) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.wareSkuRepository = wareSkuRepository;
        this.memberSkuPurchaseCountRepository = memberSkuPurchaseCountRepository;
        this.gateQuotaAdapter = gateQuotaAdapter;
    }

    @Override
    public void remove(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return;
        }
        skuLaunchConfigRepository.deleteBySkuIds(skuIds);
        wareSkuRepository.deleteBySkuIds(skuIds);
        memberSkuPurchaseCountRepository.deleteBySkuIds(skuIds);
        // clear() 刪一個不存在的 Redis key 是安全的 no-op，不用先判斷這顆 sku 是不是真的在保護中。
        skuIds.forEach(gateQuotaAdapter::clear);
    }
}
