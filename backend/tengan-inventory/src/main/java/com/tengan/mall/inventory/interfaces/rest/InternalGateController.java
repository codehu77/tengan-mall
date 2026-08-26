package com.tengan.mall.inventory.interfaces.rest;

import com.tengan.mall.inventory.application.gate.ListGateStatusesUseCase;
import com.tengan.mall.inventory.application.gate.WarmUpGatesUseCase;
import com.tengan.mall.inventory.interfaces.rest.dto.GateStatusResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.ListGateStatusResponse;
import com.tengan.mall.inventory.interfaces.rest.dto.WarmUpGatesNowResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 供 tengan-admin 呼叫，「即將開賣」流量閘門(Phase B)的後台監控/手動操作用端點。 */
@RestController
@RequestMapping("/internal/inventory/gates")
public class InternalGateController {

    private final ListGateStatusesUseCase listGateStatusesUseCase;
    private final WarmUpGatesUseCase warmUpGatesUseCase;

    public InternalGateController(ListGateStatusesUseCase listGateStatusesUseCase,
            WarmUpGatesUseCase warmUpGatesUseCase) {
        this.listGateStatusesUseCase = listGateStatusesUseCase;
        this.warmUpGatesUseCase = warmUpGatesUseCase;
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
}
