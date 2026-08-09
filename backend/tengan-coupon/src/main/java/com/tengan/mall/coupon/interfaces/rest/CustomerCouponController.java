package com.tengan.mall.coupon.interfaces.rest;

import com.tengan.mall.coupon.application.membercoupon.ListAvailableCouponsUseCase;
import com.tengan.mall.coupon.application.membercoupon.ListMyCouponsResult;
import com.tengan.mall.coupon.application.membercoupon.ListMyCouponsUseCase;
import com.tengan.mall.coupon.application.membercoupon.MyCouponView;
import com.tengan.mall.coupon.application.membercoupon.ValidateCouponCommand;
import com.tengan.mall.coupon.application.membercoupon.ValidateCouponUseCase;
import com.tengan.mall.coupon.interfaces.rest.dto.ListMyCouponsResponse;
import com.tengan.mall.coupon.interfaces.rest.dto.MyCouponResponse;
import com.tengan.mall.coupon.interfaces.rest.dto.ValidateCouponRequest;
import com.tengan.mall.coupon.interfaces.rest.dto.ValidateCouponResponse;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/** zero-trust：直接用 userJwtDecoder 驗證的 Jwt 取 sub 當 userId，不信任 Gateway 轉發的任何明文身份資訊。 */
@RestController
@RequestMapping("/api/customer/coupons")
public class CustomerCouponController {

    private final ListMyCouponsUseCase listMyCouponsUseCase;
    private final ListAvailableCouponsUseCase listAvailableCouponsUseCase;
    private final ValidateCouponUseCase validateCouponUseCase;

    public CustomerCouponController(ListMyCouponsUseCase listMyCouponsUseCase,
            ListAvailableCouponsUseCase listAvailableCouponsUseCase, ValidateCouponUseCase validateCouponUseCase) {
        this.listMyCouponsUseCase = listMyCouponsUseCase;
        this.listAvailableCouponsUseCase = listAvailableCouponsUseCase;
        this.validateCouponUseCase = validateCouponUseCase;
    }

    @GetMapping("/my")
    public ListMyCouponsResponse my(@AuthenticationPrincipal Jwt jwt) {
        return toResponse(listMyCouponsUseCase.list(userId(jwt)));
    }

    /**
     * skuIds 這次收下不使用——優惠券店鋪通用，不限定商品範圍（見開發規劃「設計決策」），保留參數是
     * 為了跟文件定義的呼叫端形狀對齊，之後如果真的要做限定商品券再接上。
     */
    @GetMapping("/available")
    public ListMyCouponsResponse available(@AuthenticationPrincipal Jwt jwt, @RequestParam BigDecimal amount,
            @RequestParam(required = false) String skuIds) {
        return toResponse(listAvailableCouponsUseCase.list(userId(jwt), amount));
    }

    @PostMapping("/validate")
    public ValidateCouponResponse validate(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody ValidateCouponRequest request) {
        var result = validateCouponUseCase
                .validate(new ValidateCouponCommand(userId(jwt), request.couponId(), request.amount()));
        return new ValidateCouponResponse(result.valid(), result.discountAmount());
    }

    private Long userId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }

    private ListMyCouponsResponse toResponse(ListMyCouponsResult result) {
        var items = result.items().stream().map(this::toResponse).toList();
        return new ListMyCouponsResponse(items);
    }

    private MyCouponResponse toResponse(MyCouponView view) {
        return new MyCouponResponse(view.id(), view.templateId(), view.templateName(), view.thresholdAmount(),
                view.discountAmount(), view.useStatus(), view.orderSn(), view.receivedAt());
    }
}
