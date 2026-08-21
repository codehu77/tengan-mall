package com.tengan.mall.seckill.infrastructure.inventory;

import com.tengan.mall.seckill.application.port.InventoryPort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryAdapter implements InventoryPort {

    private final RestClient inventoryRestClient;
    private final InventoryServiceTokenProvider tokenProvider;

    public InventoryAdapter(RestClient inventoryRestClient, InventoryServiceTokenProvider tokenProvider) {
        this.inventoryRestClient = inventoryRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void seckillDeduct(Long skuId, int count) {
        inventoryRestClient.post()
                .uri("/internal/inventory/seckill/deduct")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new SeckillDeductRequestDto(skuId, count))
                .retrieve()
                .toBodilessEntity();
    }
}
