package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.application.gate.ConfigureGateUseCase;
import com.tengan.mall.inventory.application.gate.GetGateStatusUseCase;
import com.tengan.mall.inventory.application.gate.ListGateStatusesUseCase;
import com.tengan.mall.inventory.application.gate.WarmUpGatesUseCase;
import com.tengan.mall.inventory.interfaces.rest.dto.ConfigureGateRequest;
import com.tengan.mall.inventory.interfaces.rest.dto.GateConfigResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.GateStatusResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.ListGateConfigResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.ListGateStatusResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.WarmUpGatesNowResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 供 tengan-admin 呼叫，「即將開賣」庫存流量閘門(Phase B)的後台監控/手動操作用端點。 */
@RestController
@RequestMapping("/internal/inventory/gates")
public class InternalGateController {

    private final ListGateStatusesUseCase listGateStatusesUseCase;
    private final WarmUpGatesUseCase warmUpGatesUseCase;
    private final ConfigureGateUseCase configureGateUseCase;
    private final GetGateStatusUseCase getGateStatusUseCase;

    public InternalGateController(ListGateStatusesUseCase listGateStatusesUseCase,
            WarmUpGatesUseCase warmUpGatesUseCase, ConfigureGateUseCase configureGateUseCase,
            GetGateStatusUseCase getGateStatusUseCase) {
        this.listGateStatusesUseCase = listGateStatusesUseCase;
        this.warmUpGatesUseCase = warmUpGatesUseCase;
        this.configureGateUseCase = configureGateUseCase;
        this.getGateStatusUseCase = getGateStatusUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_inventory.read')")
    public ListGateStatusResponse list() {
        var items = listGateStatusesUseCase.list().stream()
                .map(v -> new GateStatusResponse(v.skuId(), v.saleStartTime(), v.gateCloseTime(),
                        v.purchaseLimitPerUser(), v.gateProtectedStock(), v.gateWarmedAt(), v.gateSettledAt(),
                        v.currentAvailablePermits(), v.buyersCount()))
                .toList();
        return new ListGateStatusResponse(items);
    }

    /** 供 tengan-admin BFF「立即預熱」按鈕呼叫——不用等 GateWarmUpScheduler 固定的每日四個時間點，
     * demo/測試新建的商品不用乾等到下一個排程時間點才會啟動保護。完全比照 tengan-seckill 的
     * /internal/seckill/warmup-now 同樣模式，兩者互相獨立、不共用任何程式碼。 */
    @PostMapping("/warmup-now")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public WarmUpGatesNowResponse warmUpNow() {
        int count = warmUpGatesUseCase.warmUp();
        return new WarmUpGatesNowResponse(count);
    }

    /** 供 tengan-admin SPU 列表頁「防超賣保護」欄位批次回填用。 */
    @GetMapping("/by-spu")
    @PreAuthorize("hasAuthority('SCOPE_inventory.read')")
    public ListGateConfigResponse getBySpuIds(@RequestParam List<Long> spuIds) {
        var items = getGateStatusUseCase.getBySpuIds(spuIds).stream()
                .map(v -> new GateConfigResponse(v.spuId(), v.gateWarmedAt(), v.gateCloseTime()))
                .toList();
        return new ListGateConfigResponse(items);
    }

    /** 管理員在 SPU 列表頁「啟用/重設防超賣保護」，一顆 SPU 底下每顆 SKU 各呼叫一次。 */
    @PutMapping("/{skuId}")
    @PreAuthorize("hasAuthority('SCOPE_inventory.write')")
    public void configure(@PathVariable Long skuId, @RequestBody ConfigureGateRequest request) {
        configureGateUseCase.configure(skuId, request.gateCloseTime());
    }
}
