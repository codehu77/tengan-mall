package com.tengan.mall.payment.interfaces.rest;

import com.tengan.mall.payment.application.admin.ListSubscriptionPaymentsUseCase;
import com.tengan.mall.payment.application.admin.ListSubscriptionsQuery;
import com.tengan.mall.payment.application.admin.ListSubscriptionsUseCase;
import com.tengan.mall.payment.interfaces.rest.dto.SubscriptionPaymentResponse;
import com.tengan.mall.payment.interfaces.rest.dto.SubscriptionRecordListResponse;
import com.tengan.mall.payment.interfaces.rest.dto.SubscriptionRecordResponse;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 供 tengan-admin BFF 呼叫，比照 InternalPaymentController 既有寫法，唯讀端點只需要 payment.read scope。 */
@RestController
@RequestMapping("/internal/subscriptions")
public class InternalSubscriptionController {

    private final ListSubscriptionsUseCase listSubscriptionsUseCase;
    private final ListSubscriptionPaymentsUseCase listSubscriptionPaymentsUseCase;

    public InternalSubscriptionController(ListSubscriptionsUseCase listSubscriptionsUseCase,
            ListSubscriptionPaymentsUseCase listSubscriptionPaymentsUseCase) {
        this.listSubscriptionsUseCase = listSubscriptionsUseCase;
        this.listSubscriptionPaymentsUseCase = listSubscriptionPaymentsUseCase;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_payment.read')")
    public SubscriptionRecordListResponse list(@RequestParam(required = false) Long memberId,
            @RequestParam(required = false) Integer status, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = listSubscriptionsUseCase.list(new ListSubscriptionsQuery(memberId, status, page, pageSize));
        List<SubscriptionRecordResponse> items = result.items().stream()
                .map(v -> new SubscriptionRecordResponse(v.id(), v.memberId(), v.targetTier(), v.status(),
                        v.ecpayMerchantTradeNo(), v.periodAmount(), v.consecutiveFailures(), v.paidUntil(),
                        v.benefitExpiredAt(), v.createdAt(), v.cancelledAt()))
                .toList();
        return new SubscriptionRecordListResponse(items, result.total());
    }

    @GetMapping("/{id}/payments")
    @PreAuthorize("hasAuthority('SCOPE_payment.read')")
    public List<SubscriptionPaymentResponse> payments(@PathVariable Long id) {
        return listSubscriptionPaymentsUseCase.list(id).stream()
                .map(v -> new SubscriptionPaymentResponse(v.id(), v.gwsr(), v.success(), v.amount(),
                        v.totalSuccessTimes(), v.processDate(), v.createdAt()))
                .toList();
    }
}
