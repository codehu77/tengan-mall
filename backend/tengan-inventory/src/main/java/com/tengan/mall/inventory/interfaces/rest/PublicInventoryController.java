package com.tengan.mall.inventory.interfaces.rest;

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

    public PublicInventoryController(CheckStockUseCase checkStockUseCase,
            SkuLaunchConfigRepository skuLaunchConfigRepository, GateQuotaAdapter gateQuotaAdapter) {
        this.checkStockUseCase = checkStockUseCase;
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.gateQuotaAdapter = gateQuotaAdapter;
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
            var result = checkStockUseCase.check(new CheckStockCommand(List.of(new CheckStockItem(skuId, 1))));
            availableStock = result.items().get(0).availableStock();
        }

        LocalDateTime saleStartTime = launchConfig.map(c -> c.saleStartTime()).orElse(null);
        boolean purchasable = saleStartTime == null || !now.isBefore(saleStartTime);
        Integer purchaseLimitPerUser = launchConfig.map(c -> c.purchaseLimitPerUser()).orElse(null);

        return new SkuStockResponse(skuId, availableStock, saleStartTime, purchasable, purchaseLimitPerUser);
    }
}
