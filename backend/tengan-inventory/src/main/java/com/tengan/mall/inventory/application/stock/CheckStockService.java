package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import org.springframework.stereotype.Service;

@Service
public class CheckStockService implements CheckStockUseCase {

    private final WareSkuRepository wareSkuRepository;

    public CheckStockService(WareSkuRepository wareSkuRepository) {
        this.wareSkuRepository = wareSkuRepository;
    }

    @Override
    public CheckStockResult check(CheckStockCommand command) {
        var items = command.items().stream()
                .map(item -> {
                    int available = wareSkuRepository.sumAvailableStock(item.skuId());
                    return new CheckStockLineResult(item.skuId(), available >= item.count(), available);
                })
                .toList();
        boolean all = items.stream().allMatch(CheckStockLineResult::sufficient);
        return new CheckStockResult(all, items);
    }
}
