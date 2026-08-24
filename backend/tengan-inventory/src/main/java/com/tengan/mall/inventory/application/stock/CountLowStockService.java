package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import org.springframework.stereotype.Service;

/** 供 Phase 11 dashboard「低庫存 SKU 數」用。門檻定義見 {@link WareSkuRepository#LOW_STOCK_THRESHOLD}。 */
@Service
public class CountLowStockService implements CountLowStockUseCase {

    private final WareSkuRepository wareSkuRepository;

    public CountLowStockService(WareSkuRepository wareSkuRepository) {
        this.wareSkuRepository = wareSkuRepository;
    }

    @Override
    public int count() {
        return wareSkuRepository.countLowStockSkus(WareSkuRepository.LOW_STOCK_THRESHOLD);
    }
}
