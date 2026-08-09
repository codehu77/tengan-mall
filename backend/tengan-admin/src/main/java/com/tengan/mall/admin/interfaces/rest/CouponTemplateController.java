package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.CouponGrantPort;
import com.tengan.mall.admin.application.port.CouponTemplatePort;
import com.tengan.mall.admin.application.port.CreateTemplatePayload;
import com.tengan.mall.admin.application.port.GrantCouponsPayload;
import com.tengan.mall.admin.application.port.UpdateTemplatePayload;
import com.tengan.mall.admin.interfaces.rest.dto.CreateTemplateRequest;
import com.tengan.mall.admin.interfaces.rest.dto.CreateTemplateResponse;
import com.tengan.mall.admin.interfaces.rest.dto.GrantCouponsRequest;
import com.tengan.mall.admin.interfaces.rest.dto.GrantCouponsResponse;
import com.tengan.mall.admin.interfaces.rest.dto.ListTemplatesResponse;
import com.tengan.mall.admin.interfaces.rest.dto.TemplateItemResponse;
import com.tengan.mall.admin.interfaces.rest.dto.UpdateTemplateRequest;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * BFF：轉發到 tengan-coupon 的 /internal/coupons/templates + /grants，跟 {@link ProductBrandController}
 * 同樣的純代理原則。grants 不是獨立聚合根、是模板+會員名單的一次性動作，比照這裡跟模板放同一個
 * controller，不另外拆。
 */
@RestController
@RequestMapping("/api/admin/coupons")
public class CouponTemplateController {

    private final CouponTemplatePort couponTemplatePort;
    private final CouponGrantPort couponGrantPort;

    public CouponTemplateController(CouponTemplatePort couponTemplatePort, CouponGrantPort couponGrantPort) {
        this.couponTemplatePort = couponTemplatePort;
        this.couponGrantPort = couponGrantPort;
    }

    @GetMapping("/templates")
    @PreAuthorize("hasAuthority('coupon:template:read')")
    public ListTemplatesResponse listTemplates() {
        var items = couponTemplatePort.listTemplates().stream()
                .map(t -> new TemplateItemResponse(t.id(), t.name(), t.thresholdAmount(), t.discountAmount(),
                        t.totalCount(), t.issuedCount(), t.effectiveStart(), t.effectiveEnd(), t.status()))
                .toList();
        return new ListTemplatesResponse(items);
    }

    @PostMapping("/templates")
    @PreAuthorize("hasAuthority('coupon:template:write')")
    public CreateTemplateResponse createTemplate(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody CreateTemplateRequest request) {
        Long id = couponTemplatePort.createTemplate(new CreateTemplatePayload(request.name(),
                request.thresholdAmount(), request.discountAmount(), request.totalCount(), request.effectiveStart(),
                request.effectiveEnd()), operatorJwt.getTokenValue());
        return new CreateTemplateResponse(id);
    }

    @PutMapping("/templates/{id}")
    @PreAuthorize("hasAuthority('coupon:template:write')")
    public void updateTemplate(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id,
            @Valid @RequestBody UpdateTemplateRequest request) {
        couponTemplatePort.updateTemplate(id, new UpdateTemplatePayload(request.name(), request.thresholdAmount(),
                request.discountAmount(), request.totalCount(), request.effectiveStart(), request.effectiveEnd()),
                operatorJwt.getTokenValue());
    }

    @DeleteMapping("/templates/{id}")
    @PreAuthorize("hasAuthority('coupon:template:write')")
    public void delistTemplate(@AuthenticationPrincipal Jwt operatorJwt, @PathVariable Long id) {
        couponTemplatePort.delistTemplate(id, operatorJwt.getTokenValue());
    }

    @PostMapping("/grants")
    @PreAuthorize("hasAuthority('coupon:grant:write')")
    public GrantCouponsResponse grant(@AuthenticationPrincipal Jwt operatorJwt,
            @Valid @RequestBody GrantCouponsRequest request) {
        var result = couponGrantPort.grant(new GrantCouponsPayload(request.templateId(), request.userIds()),
                operatorJwt.getTokenValue());
        return new GrantCouponsResponse(result.succeededUserIds(), result.skippedUserIds());
    }
}
