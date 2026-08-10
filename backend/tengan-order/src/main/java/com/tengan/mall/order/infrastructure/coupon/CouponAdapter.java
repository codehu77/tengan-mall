package com.tengan.mall.order.infrastructure.coupon;

import com.tengan.mall.order.application.port.CouponPort;
import com.tengan.mall.order.application.port.CouponValidationResult;
import com.tengan.mall.order.infrastructure.coupon.dto.InternalValidateCouponRequestDto;
import com.tengan.mall.order.infrastructure.coupon.dto.OrderSnRequestDto;
import com.tengan.mall.order.infrastructure.coupon.dto.ValidateCouponResponseDto;
import java.math.BigDecimal;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CouponAdapter implements CouponPort {

    private final RestClient couponRestClient;
    private final CouponServiceTokenProvider tokenProvider;

    public CouponAdapter(RestClient couponRestClient, CouponServiceTokenProvider tokenProvider) {
        this.couponRestClient = couponRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public CouponValidationResult validate(Long memberId, Long couponId, BigDecimal amount) {
        ValidateCouponResponseDto response = couponRestClient.post()
                .uri("/internal/coupons/validate")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new InternalValidateCouponRequestDto(memberId, couponId, amount))
                .retrieve()
                .body(ValidateCouponResponseDto.class);
        if (response == null) {
            throw new IllegalStateException("驗證優惠券呼叫無回應: couponId=" + couponId);
        }
        return new CouponValidationResult(response.valid(), response.discountAmount());
    }

    @Override
    public void consume(Long couponId, String orderSn) {
        couponRestClient.post()
                .uri("/internal/coupons/{id}/consume", couponId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new OrderSnRequestDto(orderSn))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void revert(Long couponId, String orderSn) {
        couponRestClient.post()
                .uri("/internal/coupons/{id}/revert", couponId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new OrderSnRequestDto(orderSn))
                .retrieve()
                .toBodilessEntity();
    }
}
