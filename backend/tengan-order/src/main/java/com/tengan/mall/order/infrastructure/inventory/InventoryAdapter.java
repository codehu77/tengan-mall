package com.tengan.mall.order.infrastructure.inventory;

import com.tengan.mall.order.application.port.InventoryPort;
import com.tengan.mall.order.application.port.LockItem;
import com.tengan.mall.order.application.port.LockResult;
import com.tengan.mall.order.infrastructure.inventory.dto.LockInventoryItemDto;
import com.tengan.mall.order.infrastructure.inventory.dto.LockInventoryRequestDto;
import com.tengan.mall.order.infrastructure.inventory.dto.LockInventoryResponseDto;
import com.tengan.mall.order.infrastructure.inventory.dto.OrderSnRequestDto;
import java.util.List;
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
    public LockResult lock(String orderSn, List<LockItem> items) {
        var dtoItems = items.stream().map(i -> new LockInventoryItemDto(i.skuId(), i.count())).toList();
        LockInventoryResponseDto response = inventoryRestClient.post()
                .uri("/internal/inventory/lock")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new LockInventoryRequestDto(orderSn, dtoItems))
                .retrieve()
                .body(LockInventoryResponseDto.class);
        if (response == null) {
            throw new IllegalStateException("鎖庫存呼叫無回應: orderSn=" + orderSn);
        }
        return new LockResult(response.success(), response.shortageSkuIds());
    }

    @Override
    public void release(String orderSn) {
        inventoryRestClient.post()
                .uri("/internal/inventory/release")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new OrderSnRequestDto(orderSn))
                .retrieve()
                .toBodilessEntity();
    }
}
