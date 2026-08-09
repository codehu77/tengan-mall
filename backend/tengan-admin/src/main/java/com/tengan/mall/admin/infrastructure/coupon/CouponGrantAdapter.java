package com.tengan.mall.admin.infrastructure.coupon;

import com.tengan.mall.admin.application.port.CouponGrantPort;
import com.tengan.mall.admin.application.port.GrantCouponsPayload;
import com.tengan.mall.admin.application.port.GrantCouponsResult;
import com.tengan.mall.admin.infrastructure.coupon.dto.GrantEnvelope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CouponGrantAdapter implements CouponGrantPort {

    private static final String BASE_PATH = "/internal/coupons/grants";

    private final RestClient couponRestClient;
    private final CouponServiceTokenProvider tokenProvider;

    public CouponGrantAdapter(RestClient couponRestClient, CouponServiceTokenProvider tokenProvider) {
        this.couponRestClient = couponRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public GrantCouponsResult grant(GrantCouponsPayload payload, String operatorToken) {
        GrantEnvelope envelope = couponRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .body(GrantEnvelope.class);
        return envelope == null ? new GrantCouponsResult(java.util.List.of(), java.util.List.of())
                : new GrantCouponsResult(envelope.succeededUserIds(), envelope.skippedUserIds());
    }
}
