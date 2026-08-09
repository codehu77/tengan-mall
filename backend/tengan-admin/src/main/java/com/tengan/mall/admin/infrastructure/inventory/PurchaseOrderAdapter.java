package com.tengan.mall.admin.infrastructure.inventory;

import com.tengan.mall.admin.application.port.CreatePurchaseOrderPayload;
import com.tengan.mall.admin.application.port.PurchaseOrderDetail;
import com.tengan.mall.admin.application.port.PurchaseOrderPageResult;
import com.tengan.mall.admin.application.port.PurchaseOrderPort;
import com.tengan.mall.admin.application.port.ReceivePurchaseOrderPayload;
import com.tengan.mall.admin.infrastructure.inventory.dto.IdEnvelope;
import com.tengan.mall.admin.infrastructure.inventory.dto.PurchaseOrderListEnvelope;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PurchaseOrderAdapter implements PurchaseOrderPort {

    private static final String BASE_PATH = "/internal/inventory/purchase-orders";

    private final RestClient inventoryRestClient;
    private final InventoryServiceTokenProvider tokenProvider;

    public PurchaseOrderAdapter(RestClient inventoryRestClient, InventoryServiceTokenProvider tokenProvider) {
        this.inventoryRestClient = inventoryRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public Long createPurchaseOrder(CreatePurchaseOrderPayload payload, String operatorToken) {
        IdEnvelope envelope = inventoryRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .body(IdEnvelope.class);
        return envelope.id();
    }

    @Override
    public PurchaseOrderPageResult listPurchaseOrders(Integer status, Long wareId, int page, int pageSize) {
        String uri = UriComponentsBuilder.fromPath(BASE_PATH)
                .queryParamIfPresent("status", Optional.ofNullable(status))
                .queryParamIfPresent("wareId", Optional.ofNullable(wareId))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build().toUriString();
        PurchaseOrderListEnvelope envelope = inventoryRestClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(PurchaseOrderListEnvelope.class);
        return envelope == null ? new PurchaseOrderPageResult(List.of(), 0)
                : new PurchaseOrderPageResult(envelope.items(), envelope.total());
    }

    @Override
    public PurchaseOrderDetail getPurchaseOrderDetail(Long id) {
        return inventoryRestClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(PurchaseOrderDetail.class);
    }

    @Override
    public void receivePurchaseOrder(Long id, ReceivePurchaseOrderPayload payload, String operatorToken) {
        inventoryRestClient.post()
                .uri(BASE_PATH + "/{id}/receive", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
