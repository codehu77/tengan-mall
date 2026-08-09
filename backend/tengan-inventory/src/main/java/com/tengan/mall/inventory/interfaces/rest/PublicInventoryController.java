package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.application.stock.CheckStockCommand;
import com.tengan.mall.inventory.application.stock.CheckStockItem;
import com.tengan.mall.inventory.application.stock.CheckStockUseCase;
import com.tengan.mall.inventory.interfaces.rest.dto.SkuStockResponse;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/inventory")
public class PublicInventoryController {

    private final CheckStockUseCase checkStockUseCase;

    public PublicInventoryController(CheckStockUseCase checkStockUseCase) {
        this.checkStockUseCase = checkStockUseCase;
    }

    @GetMapping("/skus/{skuId}")
    public SkuStockResponse getSkuStock(@PathVariable Long skuId) {
        var result = checkStockUseCase.check(new CheckStockCommand(List.of(new CheckStockItem(skuId, 1))));
        return new SkuStockResponse(skuId, result.items().get(0).availableStock());
    }
}
