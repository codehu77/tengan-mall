package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.application.stock.CheckStockCommand;
import com.tengan.mall.inventory.application.stock.CheckStockItem;
import com.tengan.mall.inventory.application.stock.CheckStockUseCase;
import com.tengan.mall.inventory.interfaces.rest.dto.CheckStockItemResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.CheckStockRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.CheckStockResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/customer/inventory")
public class CustomerInventoryController {

    private final CheckStockUseCase checkStockUseCase;

    public CustomerInventoryController(CheckStockUseCase checkStockUseCase) {
        this.checkStockUseCase = checkStockUseCase;
    }

    @PostMapping("/check")
    public CheckStockResponse check(@Valid @RequestBody CheckStockRequest request) {
        var items = request.items().stream().map(i -> new CheckStockItem(i.skuId(), i.count())).toList();
        var result = checkStockUseCase.check(new CheckStockCommand(items));
        var responseItems = result.items().stream()
                .map(i -> new CheckStockItemResponse(i.skuId(), i.sufficient(), i.availableStock()))
                .toList();
        return new CheckStockResponse(result.allSufficient(), responseItems);
    }
}
