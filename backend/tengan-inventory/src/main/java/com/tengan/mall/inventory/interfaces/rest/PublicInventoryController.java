package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.application.gate.SettleGatesUseCase;
import com.tengan.mall.inventory.application.stock.CheckStockCommand;
import com.tengan.mall.inventory.application.stock.CheckStockItem;
import com.tengan.mall.inventory.application.stock.CheckStockUseCase;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.infrastructure.redis.GateQuotaAdapter;
import com.tengan.mall.inventory.interfaces.rest.dto.SkuStockResponse;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/inventory")
public class PublicInventoryController {

    private final CheckStockUseCase checkStockUseCase;
    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final GateQuotaAdapter gateQuotaAdapter;
    private final SettleGatesUseCase settleGatesUseCase;

    public PublicInventoryController(CheckStockUseCase checkStockUseCase,
            SkuLaunchConfigRepository skuLaunchConfigRepository, GateQuotaAdapter gateQuotaAdapter,
            SettleGatesUseCase settleGatesUseCase) {
        this.checkStockUseCase = checkStockUseCase;
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.gateQuotaAdapter = gateQuotaAdapter;
        this.settleGatesUseCase = settleGatesUseCase;
    }

    @GetMapping("/skus/{skuId}")
    public SkuStockResponse getSkuStock(@PathVariable Long skuId) {
        var launchConfig = skuLaunchConfigRepository.findBySkuId(skuId);
        LocalDateTime now = LocalDateTime.now();

        // 閘門保護中改讀即時 Redis 數字，讓搶購當下看到的「剩餘 N 件」是真的跟大家搶的那個數字，
        // 其餘情況（閘門未啟用/尚未開賣/已結算）維持原本的 MySQL 讀法。
        int availableStock;
        if (launchConfig.isPresent() && launchConfig.get().isGateActive(now)) {
            availableStock = gateQuotaAdapter.availablePermits(skuId);
        } else {
            // 保護窗口剛關閉、還沒結算的話，ware_sku.stock 還是保護期間開始前的舊數字（跟
            // LockInventoryService 同一個懶結算邏輯）——查詢當下順手補結算一次，不然使用者/後台
            // 會看到庫存「變回」保護前的數字，誤以為賣出去的量憑空消失了。
            if (launchConfig.isPresent() && launchConfig.get().gateWarmedAt() != null
                    && launchConfig.get().gateSettledAt() == null) {
                settleGatesUseCase.settleOne(skuId);
            }
            var result = checkStockUseCase.check(new CheckStockCommand(List.of(new CheckStockItem(skuId, 1))));
            availableStock = result.items().get(0).availableStock();
        }

        LocalDateTime saleStartTime = launchConfig.map(c -> c.saleStartTime()).orElse(null);
        boolean purchasable = saleStartTime == null || !now.isBefore(saleStartTime);
        Integer purchaseLimitPerUser = launchConfig.map(c -> c.purchaseLimitPerUser()).orElse(null);

        return new SkuStockResponse(skuId, availableStock, saleStartTime, purchasable, purchaseLimitPerUser);
    }
}
