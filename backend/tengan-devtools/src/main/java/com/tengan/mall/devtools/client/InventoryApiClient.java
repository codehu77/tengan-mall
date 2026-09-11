package com.tengan.mall.devtools.client;

import com.tengan.mall.devtools.client.dto.CreateStockRequest;
import com.tengan.mall.devtools.client.dto.Warehouse;
import com.tengan.mall.devtools.client.dto.WarehouseList;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** 呼叫 tengan-inventory 的 internal API：查倉庫（找預設倉）+ 幫新 SKU 建初始庫存列。 */
@Component
public class InventoryApiClient {

    private static final String REGISTRATION_ID = "tengan-inventory";

    private final RestClient inventoryRestClient;
    private final ServiceTokenProvider tokenProvider;
    private final AdminBotTokenProvider adminBotTokenProvider;

    public InventoryApiClient(RestClient inventoryRestClient, ServiceTokenProvider tokenProvider,
            AdminBotTokenProvider adminBotTokenProvider) {
        this.inventoryRestClient = inventoryRestClient;
        this.tokenProvider = tokenProvider;
        this.adminBotTokenProvider = adminBotTokenProvider;
    }

    public List<Warehouse> listWarehouses() {
        WarehouseList response = inventoryRestClient.get()
                .uri("/internal/inventory/warehouses")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .body(WarehouseList.class);
        return response == null ? List.of() : response.items();
    }

    public void createStock(Long wareId, Long skuId, int initialStock) {
        inventoryRestClient.post()
                .uri("/internal/inventory/skus")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .header("X-Identity-Assertion", adminBotTokenProvider.getIdentityAssertion())
                .body(new CreateStockRequest(skuId, wareId, initialStock))
                .retrieve()
                .toBodilessEntity();
    }

    private String bearerToken() {
        return "Bearer " + tokenProvider.getAccessToken(REGISTRATION_ID);
    }
}
