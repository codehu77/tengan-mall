package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.application.stock.CheckStockCommand;
import com.tengan.mall.inventory.application.stock.CheckStockItem;
import com.tengan.mall.inventory.application.stock.CheckStockUseCase;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
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

    public PublicInventoryController(CheckStockUseCase checkStockUseCase,
            SkuLaunchConfigRepository skuLaunchConfigRepository) {
        this.checkStockUseCase = checkStockUseCase;
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
    }

    @GetMapping("/skus/{skuId}")
    public SkuStockResponse getSkuStock(@PathVariable Long skuId) {
        var result = checkStockUseCase.check(new CheckStockCommand(List.of(new CheckStockItem(skuId, 1))));
        int availableStock = result.items().get(0).availableStock();

        var launchConfig = skuLaunchConfigRepository.findBySkuId(skuId);
        LocalDateTime saleStartTime = launchConfig.map(c -> c.saleStartTime()).orElse(null);
        boolean purchasable = saleStartTime == null || !LocalDateTime.now().isBefore(saleStartTime);
        Integer purchaseLimitPerUser = launchConfig.map(c -> c.purchaseLimitPerUser()).orElse(null);

        return new SkuStockResponse(skuId, availableStock, saleStartTime, purchasable, purchaseLimitPerUser);
    }
}
