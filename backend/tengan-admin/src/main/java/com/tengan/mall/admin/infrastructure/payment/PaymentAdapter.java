package com.tengan.mall.admin.infrastructure.payment;

import com.tengan.mall.admin.application.port.PaymentMethodConfigItem;
import com.tengan.mall.admin.application.port.PaymentPort;
import com.tengan.mall.admin.application.port.PaymentRecordPageResult;
import com.tengan.mall.admin.application.port.ReconcileNowResult;
import com.tengan.mall.admin.infrastructure.payment.dto.PaymentRecordListEnvelope;
import com.tengan.mall.admin.infrastructure.payment.dto.UpdatePaymentMethodStatusPayload;
import java.util.List;
import java.util.Optional;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class PaymentAdapter implements PaymentPort {

    private final RestClient paymentRestClient;
    private final PaymentServiceTokenProvider tokenProvider;

    public PaymentAdapter(RestClient paymentRestClient, PaymentServiceTokenProvider tokenProvider) {
        this.paymentRestClient = paymentRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public PaymentRecordPageResult listPaymentRecords(String orderSn, String method, int page, int pageSize) {
        String uri = UriComponentsBuilder.fromPath("/internal/payments")
                .queryParamIfPresent("orderSn", Optional.ofNullable(orderSn))
                .queryParamIfPresent("method", Optional.ofNullable(method)).queryParam("page", page)
                .queryParam("pageSize", pageSize).build().toUriString();
        PaymentRecordListEnvelope envelope = paymentRestClient.get().uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken()).retrieve()
                .body(PaymentRecordListEnvelope.class);
        return envelope == null ? new PaymentRecordPageResult(List.of(), 0)
                : new PaymentRecordPageResult(envelope.items(), envelope.total());
    }

    @Override
    public List<PaymentMethodConfigItem> listPaymentMethods() {
        List<PaymentMethodConfigItem> items = paymentRestClient.get().uri("/internal/payments/methods")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken()).retrieve()
                .body(new ParameterizedTypeReference<List<PaymentMethodConfigItem>>() {
                });
        return items == null ? List.of() : items;
    }

    @Override
    public void updatePaymentMethodStatus(String method, boolean enabled, String operatorToken) {
        paymentRestClient.put().uri("/internal/payments/methods/{method}/status", method)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(new UpdatePaymentMethodStatusPayload(enabled)).retrieve().toBodilessEntity();
    }

    @Override
    public ReconcileNowResult triggerReconcileNow() {
        ReconcileNowResult result = paymentRestClient.post().uri("/internal/reconcile/run")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken()).retrieve()
                .body(ReconcileNowResult.class);
        return result == null ? new ReconcileNowResult(0, 0, 0, 0, 0, 0, 0, 0) : result;
    }
}
