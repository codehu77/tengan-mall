package com.tengan.mall.admin.infrastructure.payment;

import com.tengan.mall.admin.application.port.SubscriptionPaymentItem;
import com.tengan.mall.admin.application.port.SubscriptionPort;
import com.tengan.mall.admin.application.port.SubscriptionRecordPageResult;
import com.tengan.mall.admin.infrastructure.payment.dto.SubscriptionRecordListEnvelope;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class SubscriptionAdapter implements SubscriptionPort {

    private final RestClient paymentRestClient;
    private final PaymentServiceTokenProvider tokenProvider;

    public SubscriptionAdapter(RestClient paymentRestClient, PaymentServiceTokenProvider tokenProvider) {
        this.paymentRestClient = paymentRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public SubscriptionRecordPageResult listSubscriptions(Long memberId, Integer status, int page, int pageSize) {
        String uri = UriComponentsBuilder.fromPath("/internal/subscriptions")
                .queryParamIfPresent("memberId", Optional.ofNullable(memberId))
                .queryParamIfPresent("status", Optional.ofNullable(status)).queryParam("page", page)
                .queryParam("pageSize", pageSize).build().toUriString();
        SubscriptionRecordListEnvelope envelope = paymentRestClient.get().uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken()).retrieve()
                .body(SubscriptionRecordListEnvelope.class);
        return envelope == null ? new SubscriptionRecordPageResult(List.of(), 0)
                : new SubscriptionRecordPageResult(envelope.items(), envelope.total());
    }

    @Override
    public List<SubscriptionPaymentItem> listSubscriptionPayments(Long subscriptionId) {
        List<SubscriptionPaymentItem> items = paymentRestClient.get()
                .uri("/internal/subscriptions/{id}/payments", subscriptionId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken()).retrieve()
                .body(new ParameterizedTypeReference<List<SubscriptionPaymentItem>>() {
                });
        return items == null ? List.of() : items;
    }
}
