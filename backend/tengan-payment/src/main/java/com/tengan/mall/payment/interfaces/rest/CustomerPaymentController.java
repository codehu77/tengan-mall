package com.tengan.mall.payment.interfaces.rest;

import com.tengan.mall.payment.application.linepay.ConfirmLinePayPaymentCommand;
import com.tengan.mall.payment.application.linepay.ConfirmLinePayPaymentUseCase;
import com.tengan.mall.payment.application.payment.GetEnabledPaymentMethodsUseCase;
import com.tengan.mall.payment.application.payment.GetPaymentStatusUseCase;
import com.tengan.mall.payment.application.payment.InitiatePaymentCommand;
import com.tengan.mall.payment.application.payment.InitiatePaymentUseCase;
import com.tengan.mall.payment.interfaces.rest.dto.EcpayFormResponse;
import com.tengan.mall.payment.interfaces.rest.dto.InitiatePaymentRequest;
import com.tengan.mall.payment.interfaces.rest.dto.InitiatePaymentResponse;
import com.tengan.mall.payment.interfaces.rest.dto.LinePayConfirmRequest;
import com.tengan.mall.payment.interfaces.rest.dto.PaymentMethodsResponse;
import com.tengan.mall.payment.interfaces.rest.dto.PaymentStatusResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** zero-trust：直接用 userJwtDecoder 驗證的 Jwt 取 sub 當 memberId，比照 tengan-order/tengan-coupon 既有模式。 */
@RestController
@RequestMapping("/api/customer/payments")
public class CustomerPaymentController {

    private final GetEnabledPaymentMethodsUseCase getEnabledPaymentMethodsUseCase;
    private final InitiatePaymentUseCase initiatePaymentUseCase;
    private final GetPaymentStatusUseCase getPaymentStatusUseCase;
    private final ConfirmLinePayPaymentUseCase confirmLinePayPaymentUseCase;

    public CustomerPaymentController(GetEnabledPaymentMethodsUseCase getEnabledPaymentMethodsUseCase,
            InitiatePaymentUseCase initiatePaymentUseCase, GetPaymentStatusUseCase getPaymentStatusUseCase,
            ConfirmLinePayPaymentUseCase confirmLinePayPaymentUseCase) {
        this.getEnabledPaymentMethodsUseCase = getEnabledPaymentMethodsUseCase;
        this.initiatePaymentUseCase = initiatePaymentUseCase;
        this.getPaymentStatusUseCase = getPaymentStatusUseCase;
        this.confirmLinePayPaymentUseCase = confirmLinePayPaymentUseCase;
    }

    @GetMapping("/methods")
    public PaymentMethodsResponse methods() {
        return new PaymentMethodsResponse(getEnabledPaymentMethodsUseCase.getEnabledMethods());
    }

    @PutMapping("/{orderSn}")
    public InitiatePaymentResponse initiate(@AuthenticationPrincipal Jwt jwt, @PathVariable String orderSn,
            @Valid @RequestBody InitiatePaymentRequest request) {
        var result = initiatePaymentUseCase
                .initiate(new InitiatePaymentCommand(orderSn, memberId(jwt), request.method()));
        EcpayFormResponse ecpayForm = result.ecpayForm() == null ? null
                : new EcpayFormResponse(result.ecpayForm().actionUrl(), result.ecpayForm().fields());
        return new InitiatePaymentResponse(result.method(), ecpayForm, result.linePayPaymentUrl());
    }

    @GetMapping("/{orderSn}")
    public PaymentStatusResponse status(@PathVariable String orderSn) {
        var view = getPaymentStatusUseCase.getStatus(orderSn);
        return new PaymentStatusResponse(view.orderSn(), view.method(), view.status(), view.amount());
    }

    @PostMapping("/{orderSn}/linepay-confirm")
    public ResponseEntity<Void> linePayConfirm(@AuthenticationPrincipal Jwt jwt, @PathVariable String orderSn,
            @Valid @RequestBody LinePayConfirmRequest request) {
        confirmLinePayPaymentUseCase
                .confirm(new ConfirmLinePayPaymentCommand(orderSn, memberId(jwt), request.transactionId()));
        return ResponseEntity.noContent().build();
    }

    private Long memberId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
