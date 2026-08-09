package com.tengan.mall.admin.infrastructure.inventory;

import com.tengan.mall.admin.application.port.AdjustStockPayload;
import com.tengan.mall.admin.application.port.CreateStockPayload;
import com.tengan.mall.admin.application.port.InventoryStockPort;
import com.tengan.mall.admin.application.port.SkuStockPageResult;
import com.tengan.mall.admin.infrastructure.inventory.dto.SkuStockListEnvelope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class InventoryStockAdapter implements InventoryStockPort {

    private static final String BASE_PATH = "/internal/inventory/skus";

    private final RestClient inventoryRestClient;
    private final InventoryServiceTokenProvider tokenProvider;

    public InventoryStockAdapter(RestClient inventoryRestClient, InventoryServiceTokenProvider tokenProvider) {
        this.inventoryRestClient = inventoryRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public SkuStockPageResult listSkus(Long wareId, Long skuIdKeyword, int page, int pageSize) {
        String uri = UriComponentsBuilder.fromPath(BASE_PATH)
                .queryParamIfPresent("wareId", java.util.Optional.ofNullable(wareId))
                .queryParamIfPresent("keyword", java.util.Optional.ofNullable(skuIdKeyword))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build().toUriString();
        SkuStockListEnvelope envelope = inventoryRestClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SkuStockListEnvelope.class);
        return envelope == null ? new SkuStockPageResult(java.util.List.of(), 0)
                : new SkuStockPageResult(envelope.items(), envelope.total());
    }

    @Override
    public void createStock(CreateStockPayload payload, String operatorToken) {
        inventoryRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void adjustStock(Long skuId, AdjustStockPayload payload, String operatorToken) {
        inventoryRestClient.put()
                .uri(BASE_PATH + "/{skuId}", skuId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
