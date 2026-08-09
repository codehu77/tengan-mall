package com.tengan.mall.admin.infrastructure.coupon;

import com.tengan.mall.admin.application.port.CouponTemplatePort;
import com.tengan.mall.admin.application.port.CreateTemplatePayload;
import com.tengan.mall.admin.application.port.TemplateItem;
import com.tengan.mall.admin.application.port.UpdateTemplatePayload;
import com.tengan.mall.admin.infrastructure.coupon.dto.IdEnvelope;
import com.tengan.mall.admin.infrastructure.coupon.dto.TemplateListEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CouponTemplateAdapter implements CouponTemplatePort {

    private static final String BASE_PATH = "/internal/coupons/templates";

    private final RestClient couponRestClient;
    private final CouponServiceTokenProvider tokenProvider;

    public CouponTemplateAdapter(RestClient couponRestClient, CouponServiceTokenProvider tokenProvider) {
        this.couponRestClient = couponRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<TemplateItem> listTemplates() {
        TemplateListEnvelope envelope = couponRestClient.get()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(TemplateListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long createTemplate(CreateTemplatePayload payload, String operatorToken) {
        IdEnvelope envelope = couponRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .body(IdEnvelope.class);
        return envelope.id();
    }

    @Override
    public void updateTemplate(Long id, UpdateTemplatePayload payload, String operatorToken) {
        couponRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void delistTemplate(Long id, String operatorToken) {
        couponRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }
}
