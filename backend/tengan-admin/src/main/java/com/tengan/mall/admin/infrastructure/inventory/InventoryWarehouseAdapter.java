package com.tengan.mall.admin.infrastructure.inventory;

import com.tengan.mall.admin.application.port.CreateWarehousePayload;
import com.tengan.mall.admin.application.port.InventoryWarehousePort;
import com.tengan.mall.admin.application.port.WarehouseItem;
import com.tengan.mall.admin.infrastructure.inventory.dto.IdEnvelope;
import com.tengan.mall.admin.infrastructure.inventory.dto.WarehouseListEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryWarehouseAdapter implements InventoryWarehousePort {

    private static final String BASE_PATH = "/internal/inventory/warehouses";

    private final RestClient inventoryRestClient;
    private final InventoryServiceTokenProvider tokenProvider;

    public InventoryWarehouseAdapter(RestClient inventoryRestClient, InventoryServiceTokenProvider tokenProvider) {
        this.inventoryRestClient = inventoryRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<WarehouseItem> listWarehouses() {
        WarehouseListEnvelope envelope = inventoryRestClient.get()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(WarehouseListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long createWarehouse(CreateWarehousePayload payload, String operatorToken) {
        IdEnvelope envelope = inventoryRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .body(IdEnvelope.class);
        return envelope.id();
    }
}
