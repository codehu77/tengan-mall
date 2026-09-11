package com.tengan.mall.admin.infrastructure.order;

import com.tengan.mall.admin.application.port.FreightRuleItem;
import com.tengan.mall.admin.application.port.OrderDetail;
import com.tengan.mall.admin.application.port.OrderPageResult;
import com.tengan.mall.admin.application.port.OrderPort;
import com.tengan.mall.admin.application.port.OrderTodayStats;
import com.tengan.mall.admin.application.port.RevenueTrendPoint;
import com.tengan.mall.admin.infrastructure.order.dto.AdminCancelOrderPayload;
import com.tengan.mall.admin.infrastructure.order.dto.OrderListEnvelope;
import com.tengan.mall.admin.infrastructure.order.dto.RevenueTrendEnvelope;
import java.time.Instant;
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
    public OrderPageResult listOrders(Integer status, Instant from, Instant to, int page, int pageSize) {
        String uri = UriComponentsBuilder.fromPath("/internal/orders")
                .queryParamIfPresent("status", Optional.ofNullable(status))
                .queryParamIfPresent("from", Optional.ofNullable(from))
                .queryParamIfPresent("to", Optional.ofNullable(to))
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
    public OrderTodayStats getTodayStats() {
        return orderRestClient.get()
                .uri("/internal/orders/stats/today")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(OrderTodayStats.class);
    }

    @Override
    public List<RevenueTrendPoint> getRevenueTrend(int days) {
        RevenueTrendEnvelope envelope = orderRestClient.get()
                .uri("/internal/orders/stats/revenue-trend?days={days}", days)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(RevenueTrendEnvelope.class);
        return envelope == null ? List.of() : envelope.points();
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

    @Override
    public FreightRuleItem getFreightRule() {
        return orderRestClient.get()
                .uri("/internal/orders/freight-rule")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(FreightRuleItem.class);
    }

    @Override
    public void updateFreightRule(FreightRuleItem item, String operatorToken) {
        orderRestClient.put()
                .uri("/internal/orders/freight-rule")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(item)
                .retrieve()
                .toBodilessEntity();
    }
}
