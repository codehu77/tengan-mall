package com.tengan.mall.payment.interfaces.rest;

import com.tengan.mall.payment.application.subscription.CancelSubscriptionCommand;
import com.tengan.mall.payment.application.subscription.CancelSubscriptionUseCase;
import com.tengan.mall.payment.application.subscription.GetMySubscriptionUseCase;
import com.tengan.mall.payment.application.subscription.SubscribeCommand;
import com.tengan.mall.payment.application.subscription.SubscribeUseCase;
import com.tengan.mall.payment.interfaces.rest.dto.EcpayFormResponse;
import com.tengan.mall.payment.interfaces.rest.dto.MySubscriptionResponse;
import com.tengan.mall.payment.interfaces.rest.dto.SubscribeRequest;
import com.tengan.mall.payment.interfaces.rest.dto.SubscribeResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** zero-trust：直接用 userJwtDecoder 驗證的 Jwt 取 sub 當 memberId，比照 CustomerPaymentController 既有模式。 */
@RestController
@RequestMapping("/api/customer/payments/subscriptions")
public class CustomerSubscriptionController {

    private final SubscribeUseCase subscribeUseCase;
    private final CancelSubscriptionUseCase cancelSubscriptionUseCase;
    private final GetMySubscriptionUseCase getMySubscriptionUseCase;

    public CustomerSubscriptionController(SubscribeUseCase subscribeUseCase,
            CancelSubscriptionUseCase cancelSubscriptionUseCase, GetMySubscriptionUseCase getMySubscriptionUseCase) {
        this.subscribeUseCase = subscribeUseCase;
        this.cancelSubscriptionUseCase = cancelSubscriptionUseCase;
        this.getMySubscriptionUseCase = getMySubscriptionUseCase;
    }

    @PostMapping
    public SubscribeResponse subscribe(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody SubscribeRequest request) {
        var result = subscribeUseCase.subscribe(new SubscribeCommand(memberId(jwt), request.targetTier()));
        var form = new EcpayFormResponse(result.ecpayForm().actionUrl(), result.ecpayForm().fields());
        return new SubscribeResponse(result.subscriptionId(), form);
    }

    @GetMapping("/me")
    public MySubscriptionResponse me(@AuthenticationPrincipal Jwt jwt) {
        var view = getMySubscriptionUseCase.get(memberId(jwt));
        return new MySubscriptionResponse(view.subscribed(), view.targetTier(), view.status(), view.paidUntil(),
                view.autoRenew());
    }

    @PutMapping("/cancel")
    public ResponseEntity<Void> cancel(@AuthenticationPrincipal Jwt jwt) {
        cancelSubscriptionUseCase.cancel(new CancelSubscriptionCommand(memberId(jwt)));
        return ResponseEntity.noContent().build();
    }

    private Long memberId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
