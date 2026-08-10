package com.tengan.mall.coupon.interfaces.rest;

import com.tengan.mall.coupon.application.membercoupon.ConsumeCouponCommand;
import com.tengan.mall.coupon.application.membercoupon.ConsumeCouponUseCase;
import com.tengan.mall.coupon.application.membercoupon.GrantCouponsCommand;
import com.tengan.mall.coupon.application.membercoupon.GrantCouponsUseCase;
import com.tengan.mall.coupon.application.membercoupon.RevertCouponCommand;
import com.tengan.mall.coupon.application.membercoupon.RevertCouponUseCase;
import com.tengan.mall.coupon.application.template.CreateTemplateCommand;
import com.tengan.mall.coupon.application.template.CreateTemplateUseCase;
import com.tengan.mall.coupon.application.template.DelistTemplateCommand;
import com.tengan.mall.coupon.application.template.DelistTemplateUseCase;
import com.tengan.mall.coupon.application.template.ListTemplatesUseCase;
import com.tengan.mall.coupon.application.template.UpdateTemplateCommand;
import com.tengan.mall.coupon.application.template.UpdateTemplateUseCase;
import com.tengan.mall.coupon.application.membercoupon.ValidateCouponCommand;
import com.tengan.mall.coupon.application.membercoupon.ValidateCouponUseCase;
import com.tengan.mall.coupon.interfaces.rest.dto.CreateTemplateRequest;
import com.tengan.mall.coupon.interfaces.rest.dto.CreateTemplateResponse;
import com.tengan.mall.coupon.interfaces.rest.dto.GrantCouponsRequest;
import com.tengan.mall.coupon.interfaces.rest.dto.GrantCouponsResponse;
import com.tengan.mall.coupon.interfaces.rest.dto.InternalValidateCouponRequest;
import com.tengan.mall.coupon.interfaces.rest.dto.ListTemplatesResponse;
import com.tengan.mall.coupon.interfaces.rest.dto.OrderSnRequest;
import com.tengan.mall.coupon.interfaces.rest.dto.TemplateResponse;
import com.tengan.mall.coupon.interfaces.rest.dto.UpdateTemplateRequest;
import com.tengan.mall.coupon.interfaces.rest.dto.ValidateCouponResponse;
import com.tengan.mall.jwt.IdentityAssertionVerifier;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/internal/coupons")
public class InternalCouponController {

    private final ConsumeCouponUseCase consumeCouponUseCase;
    private final RevertCouponUseCase revertCouponUseCase;
    private final ListTemplatesUseCase listTemplatesUseCase;
    private final CreateTemplateUseCase createTemplateUseCase;
    private final UpdateTemplateUseCase updateTemplateUseCase;
    private final DelistTemplateUseCase delistTemplateUseCase;
    private final GrantCouponsUseCase grantCouponsUseCase;
    private final ValidateCouponUseCase validateCouponUseCase;
    private final IdentityAssertionVerifier adminIdentityAssertionVerifier;

    public InternalCouponController(ConsumeCouponUseCase consumeCouponUseCase,
            RevertCouponUseCase revertCouponUseCase, ListTemplatesUseCase listTemplatesUseCase,
            CreateTemplateUseCase createTemplateUseCase, UpdateTemplateUseCase updateTemplateUseCase,
            DelistTemplateUseCase delistTemplateUseCase, GrantCouponsUseCase grantCouponsUseCase,
            ValidateCouponUseCase validateCouponUseCase,
            @Qualifier("adminIdentityAssertionVerifier") IdentityAssertionVerifier adminIdentityAssertionVerifier) {
        this.consumeCouponUseCase = consumeCouponUseCase;
        this.revertCouponUseCase = revertCouponUseCase;
        this.listTemplatesUseCase = listTemplatesUseCase;
        this.createTemplateUseCase = createTemplateUseCase;
        this.updateTemplateUseCase = updateTemplateUseCase;
        this.delistTemplateUseCase = delistTemplateUseCase;
        this.grantCouponsUseCase = grantCouponsUseCase;
        this.validateCouponUseCase = validateCouponUseCase;
        this.adminIdentityAssertionVerifier = adminIdentityAssertionVerifier;
    }

    @PostMapping("/{id}/consume")
    @PreAuthorize("hasAuthority('SCOPE_coupon.write')")
    public ResponseEntity<Void> consume(@PathVariable Long id, @Valid @RequestBody OrderSnRequest request) {
        consumeCouponUseCase.consume(new ConsumeCouponCommand(id, request.orderSn()));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/revert")
    @PreAuthorize("hasAuthority('SCOPE_coupon.write')")
    public ResponseEntity<Void> revert(@PathVariable Long id, @Valid @RequestBody OrderSnRequest request) {
        revertCouponUseCase.revert(new RevertCouponCommand(id, request.orderSn()));
        return ResponseEntity.noContent().build();
    }

    /**
     * 給 tengan-order 下單時重新核算折扣用，直接複用 CustomerCouponController 也在呼叫的
     * ValidateCouponUseCase（本身就不含 HTTP/JWT 解析邏輯），userId 改由呼叫端明確帶入 body。
     */
    @PostMapping("/validate")
    @PreAuthorize("hasAuthority('SCOPE_coupon.read')")
    public ValidateCouponResponse validate(@Valid @RequestBody InternalValidateCouponRequest request) {
        var result = validateCouponUseCase
                .validate(new ValidateCouponCommand(request.userId(), request.couponId(), request.amount()));
        return new ValidateCouponResponse(result.valid(), result.discountAmount());
    }

    @GetMapping("/templates")
    @PreAuthorize("hasAuthority('SCOPE_coupon.read')")
    public ListTemplatesResponse listTemplates() {
        var items = listTemplatesUseCase.list().items().stream()
                .map(t -> new TemplateResponse(t.id(), t.name(), t.thresholdAmount(), t.discountAmount(),
                        t.totalCount(), t.issuedCount(), t.effectiveStart(), t.effectiveEnd(), t.status()))
                .toList();
        return new ListTemplatesResponse(items);
    }

    @PostMapping("/templates")
    @PreAuthorize("hasAuthority('SCOPE_coupon.write')")
    public ResponseEntity<CreateTemplateResponse> createTemplate(
            @RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody CreateTemplateRequest request) {
        var result = createTemplateUseCase.create(new CreateTemplateCommand(operator(identityAssertion),
                request.name(), request.thresholdAmount(), request.discountAmount(), request.totalCount(),
                request.effectiveStart(), request.effectiveEnd()));
        return ResponseEntity.status(HttpStatus.CREATED).body(new CreateTemplateResponse(result.id()));
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasAuthority('SCOPE_coupon.write')")
    public ResponseEntity<Void> updateTemplate(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id, @Valid @RequestBody UpdateTemplateRequest request) {
        updateTemplateUseCase.update(new UpdateTemplateCommand(operator(identityAssertion), id, request.name(),
                request.thresholdAmount(), request.discountAmount(), request.totalCount(), request.effectiveStart(),
                request.effectiveEnd()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasAuthority('SCOPE_coupon.write')")
    public ResponseEntity<Void> delistTemplate(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @PathVariable Long id) {
        delistTemplateUseCase.delist(new DelistTemplateCommand(operator(identityAssertion), id));
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/grants")
    @PreAuthorize("hasAuthority('SCOPE_coupon.write')")
    public GrantCouponsResponse grant(@RequestHeader("X-Identity-Assertion") String identityAssertion,
            @Valid @RequestBody GrantCouponsRequest request) {
        var result = grantCouponsUseCase
                .grant(new GrantCouponsCommand(operator(identityAssertion), request.templateId(), request.userIds()));
        return new GrantCouponsResponse(result.succeededUserIds(), result.skippedUserIds());
    }

    private String operator(String identityAssertion) {
        return adminIdentityAssertionVerifier.verify(identityAssertion).getClaimAsString("username");
    }
}
