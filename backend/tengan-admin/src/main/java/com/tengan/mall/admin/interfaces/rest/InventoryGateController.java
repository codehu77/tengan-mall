package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.GateStatusItem;
import com.tengan.mall.admin.application.port.InventoryGatePort;
import com.tengan.mall.admin.application.port.ProductSkuPort;
import com.tengan.mall.admin.application.port.SkuItem;
import com.tengan.mall.admin.interfaces.rest.dto.GateStatusResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListGateStatusResponse;
import com.tengan.mall.admin.interfaces.rest.dto.WarmUpNowResponse;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BFF：轉發到 tengan-inventory 的 /internal/inventory/gates，跟 {@link InventoryStockController}
 * 同樣的純代理原則——即將開賣 Phase B 流量閘門的後台監控頁用，讓管理員看得到預熱/結算狀態，
 * 也不用乾等 GateWarmUpScheduler 固定的每日四個時間點（比照秒殺「立即預熱」按鈕同樣模式）。
 */
@RestController
@RequestMapping("/api/admin/inventory/gates")
public class InventoryGateController {

    private final InventoryGatePort inventoryGatePort;
    private final ProductSkuPort productSkuPort;

    public InventoryGateController(InventoryGatePort inventoryGatePort, ProductSkuPort productSkuPort) {
        this.inventoryGatePort = inventoryGatePort;
        this.productSkuPort = productSkuPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('inventory:gate:read')")
    public ListGateStatusResponse list() {
        var gates = inventoryGatePort.listGates();
        var skuIds = gates.stream().map(GateStatusItem::skuId).distinct().toList();
        Map<Long, SkuItem> skuById = productSkuPort.batchGet(skuIds).stream()
                .collect(Collectors.toMap(SkuItem::id, Function.identity()));
        var items = gates.stream()
                .map(g -> {
                    SkuItem sku = skuById.get(g.skuId());
                    return new GateStatusResponse(g.skuId(), sku == null ? null : sku.spuId(),
                            sku == null ? null : sku.name(), sku == null ? null : sku.mainImage(),
                            g.saleStartTime(), g.gateCloseTime(), g.purchaseLimitPerUser(), g.gateProtectedStock(),
                            g.gateWarmedAt(), g.gateSettledAt(), g.currentAvailablePermits(), g.buyersCount());
                })
                .toList();
        return new ListGateStatusResponse(items);
    }

    @PostMapping("/warmup-now")
    @PreAuthorize("hasAuthority('inventory:gate:warmup')")
    public WarmUpNowResponse warmUpNow() {
        return new WarmUpNowResponse(inventoryGatePort.triggerWarmUpNow());
    }
}
