package com.tengan.mall.payment.interfaces.rest;

import com.tengan.mall.payment.application.admin.ListPaymentMethodConfigsUseCase;
import com.tengan.mall.payment.application.admin.ListPaymentRecordsQuery;
import com.tengan.mall.payment.application.admin.ListPaymentRecordsUseCase;
import com.tengan.mall.payment.application.admin.UpdatePaymentMethodStatusCommand;
import com.tengan.mall.payment.application.admin.UpdatePaymentMethodStatusUseCase;
import com.tengan.mall.payment.interfaces.rest.dto.PaymentMethodConfigResponse;
import com.tengan.mall.payment.interfaces.rest.dto.PaymentRecordListResponse;
import com.tengan.mall.payment.interfaces.rest.dto.PaymentRecordResponse;
import com.tengan.mall.payment.interfaces.rest.dto.UpdatePaymentMethodStatusRequest;
import com.tengan.mall.jwt.IdentityAssertionVerifier;
import java.util.List;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** 供 tengan-admin BFF 呼叫，比照 InternalOrderController 既有寫法。 */
@RestController
@RequestMapping("/internal/payments")
public class InternalPaymentController {

    private final ListPaymentRecordsUseCase listPaymentRecordsUseCase;
    private final ListPaymentMethodConfigsUseCase listPaymentMethodConfigsUseCase;
    private final UpdatePaymentMethodStatusUseCase updatePaymentMethodStatusUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalPaymentController(ListPaymentRecordsUseCase listPaymentRecordsUseCase,
            ListPaymentMethodConfigsUseCase listPaymentMethodConfigsUseCase,
            UpdatePaymentMethodStatusUseCase updatePaymentMethodStatusUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.listPaymentRecordsUseCase = listPaymentRecordsUseCase;
        this.listPaymentMethodConfigsUseCase = listPaymentMethodConfigsUseCase;
        this.updatePaymentMethodStatusUseCase = updatePaymentMethodStatusUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_payment.read')")
    public PaymentRecordListResponse list(@RequestParam(required = false) String orderSn,
            @RequestParam(required = false) String method, @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int pageSize) {
        var result = listPaymentRecordsUseCase.list(new ListPaymentRecordsQuery(orderSn, method, page, pageSize));
        List<PaymentRecordResponse> items = result.items().stream()
                .map(v -> new PaymentRecordResponse(v.id(), v.orderSn(), v.memberId(), v.method(), v.amount(),
                        v.status(), v.gatewayTradeNo(), v.paidAt(), v.createdAt()))
                .toList();
        return new PaymentRecordListResponse(items, result.total());
    }

    @GetMapping("/methods")
    @PreAuthorize("hasAuthority('SCOPE_payment.read')")
    public List<PaymentMethodConfigResponse> methods() {
        return listPaymentMethodConfigsUseCase.list().stream()
                .map(c -> new PaymentMethodConfigResponse(c.method(), c.enabled())).toList();
    }

    @PutMapping("/methods/{method}/status")
    @PreAuthorize("hasAuthority('SCOPE_payment.write')")
    public ResponseEntity<Void> updateMethodStatus(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable String method, @RequestBody UpdatePaymentMethodStatusRequest request) {
        updatePaymentMethodStatusUseCase
                .update(new UpdatePaymentMethodStatusCommand(operator(identityAssertion), method, request.enabled()));
        return ResponseEntity.noContent().build();
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
