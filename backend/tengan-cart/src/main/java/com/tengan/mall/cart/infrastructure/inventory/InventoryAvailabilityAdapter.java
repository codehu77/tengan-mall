package com.tengan.mall.cart.infrastructure.inventory;

import com.tengan.mall.cart.application.cart.InventoryAvailabilityPort;
import com.tengan.mall.cart.infrastructure.inventory.dto.SkuStockDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryAvailabilityAdapter implements InventoryAvailabilityPort {

    private final RestClient inventoryRestClient;

    public InventoryAvailabilityAdapter(RestClient inventoryRestClient) {
        this.inventoryRestClient = inventoryRestClient;
    }

    @Override
    public boolean isPurchasable(Long skuId) {
        SkuStockDto dto = inventoryRestClient.get()
                .uri("/api/public/inventory/skus/{skuId}", skuId)
                .retrieve()
                .body(SkuStockDto.class);
        return dto == null || dto.purchasable();
    }
}
