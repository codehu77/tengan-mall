package com.tengan.mall.admin.infrastructure.inventory;

import com.tengan.mall.admin.application.port.ConfigureGatePayload;
import com.tengan.mall.admin.application.port.GateConfigItem;
import com.tengan.mall.admin.application.port.GateStatusItem;
import com.tengan.mall.admin.application.port.InventoryGatePort;
import com.tengan.mall.admin.infrastructure.inventory.dto.GateConfigEnvelope;
import com.tengan.mall.admin.infrastructure.inventory.dto.GateStatusListEnvelope;
import com.tengan.mall.admin.infrastructure.inventory.dto.WarmUpGatesNowEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InventoryGateAdapter implements InventoryGatePort {

    private static final String BASE_PATH = "/internal/inventory/gates";

    private final RestClient inventoryRestClient;
    private final InventoryServiceTokenProvider tokenProvider;

    public InventoryGateAdapter(RestClient inventoryRestClient, InventoryServiceTokenProvider tokenProvider) {
        this.inventoryRestClient = inventoryRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<GateStatusItem> listGates() {
        GateStatusListEnvelope envelope = inventoryRestClient.get()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(GateStatusListEnvelope.class);
        if (envelope == null) {
            return List.of();
        }
        return envelope.items().stream()
                .map(i -> new GateStatusItem(i.skuId(), i.saleStartTime(), i.gateCloseTime(),
                        i.purchaseLimitPerUser(), i.gateProtectedStock(), i.gateWarmedAt(), i.gateSettledAt(),
                        i.currentAvailablePermits(), i.buyersCount()))
                .toList();
    }

    @Override
    public int triggerWarmUpNow() {
        WarmUpGatesNowEnvelope envelope = inventoryRestClient.post()
                .uri(BASE_PATH + "/warmup-now")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(WarmUpGatesNowEnvelope.class);
        return envelope == null ? 0 : envelope.count();
    }

    @Override
    public GateConfigItem getGate(Long skuId) {
        GateConfigEnvelope envelope = inventoryRestClient.get()
                .uri(BASE_PATH + "/{skuId}", skuId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(GateConfigEnvelope.class);
        if (envelope == null) {
            return new GateConfigItem(skuId, false, false, null, null, null, null);
        }
        return new GateConfigItem(envelope.skuId(), envelope.synced(), envelope.trafficGateEnabled(),
                envelope.saleStartTime(), envelope.gateCloseTime(), envelope.gateWarmedAt(),
                envelope.gateSettledAt());
    }

    @Override
    public void configureGate(Long skuId, ConfigureGatePayload payload, String operatorToken) {
        inventoryRestClient.put()
                .uri(BASE_PATH + "/{skuId}", skuId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
