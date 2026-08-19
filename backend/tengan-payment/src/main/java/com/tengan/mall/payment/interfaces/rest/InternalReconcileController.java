package com.tengan.mall.payment.interfaces.rest;

import com.tengan.mall.payment.application.reconcile.TriggerReconcileNowUseCase;
import com.tengan.mall.payment.interfaces.rest.dto.ReconcileNowResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 供 tengan-admin BFF「立即查帳」按鈕呼叫，比照 InternalSearchController 的重建索引寫法。 */
@RestController
@RequestMapping("/internal/reconcile")
public class InternalReconcileController {

    private final TriggerReconcileNowUseCase triggerReconcileNowUseCase;

    public InternalReconcileController(TriggerReconcileNowUseCase triggerReconcileNowUseCase) {
        this.triggerReconcileNowUseCase = triggerReconcileNowUseCase;
    }

    @PostMapping("/run")
    @PreAuthorize("hasAuthority('SCOPE_payment.write')")
    public ReconcileNowResponse run() {
        var result = triggerReconcileNowUseCase.run();
        return new ReconcileNowResponse(result.paymentChecked(), result.paymentConverged(), result.paymentFailed(),
                result.subscriptionChecked(), result.subscriptionConverged(), result.subscriptionFailed(),
                result.renewalChecked(), result.renewalRecovered());
    }
}
