package com.tengan.mall.admin.infrastructure.order;

import com.tengan.mall.admin.application.port.OrderDetail;
import com.tengan.mall.admin.application.port.OrderPageResult;
import com.tengan.mall.admin.application.port.OrderPort;
import com.tengan.mall.admin.infrastructure.order.dto.AdminCancelOrderPayload;
import com.tengan.mall.admin.infrastructure.order.dto.OrderListEnvelope;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class OrderAdapter implements OrderPort {

    private final RestClient orderRestClient;
    private final OrderServiceTokenProvider tokenProvider;

    public OrderAdapter(RestClient orderRestClient, OrderServiceTokenProvider tokenProvider) {
        this.orderRestClient = orderRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public OrderPageResult listOrders(Integer status, int page, int pageSize) {
        String uri = UriComponentsBuilder.fromPath("/internal/orders")
                .queryParamIfPresent("status", Optional.ofNullable(status))
                .queryParam("page", page)
                .queryParam("pageSize", pageSize)
                .build().toUriString();
        OrderListEnvelope envelope = orderRestClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(OrderListEnvelope.class);
        return envelope == null ? new OrderPageResult(List.of(), 0)
                : new OrderPageResult(envelope.items(), envelope.total());
    }

    @Override
    public OrderDetail getOrderDetail(String orderSn) {
        return orderRestClient.get()
                .uri("/internal/orders/{orderSn}", orderSn)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(OrderDetail.class);
    }

    @Override
    public void shipOrder(String orderSn, String operatorToken) {
        orderRestClient.put()
                .uri("/internal/orders/{orderSn}/ship", orderSn)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void cancelOrder(String orderSn, String reason, String operatorToken) {
        orderRestClient.put()
                .uri("/internal/orders/{orderSn}/cancel", orderSn)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(new AdminCancelOrderPayload(reason))
                .retrieve()
                .toBodilessEntity();
    }
}
