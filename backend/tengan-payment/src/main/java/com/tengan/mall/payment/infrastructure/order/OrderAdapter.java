package com.tengan.mall.payment.infrastructure.order;

import com.tengan.mall.payment.application.port.OrderDetailForPayment;
import com.tengan.mall.payment.application.port.OrderPort;
import com.tengan.mall.payment.infrastructure.order.dto.OrderDetailDto;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class OrderAdapter implements OrderPort {

    private final RestClient orderRestClient;
    private final OrderServiceTokenProvider tokenProvider;

    public OrderAdapter(RestClient orderRestClient, OrderServiceTokenProvider tokenProvider) {
        this.orderRestClient = orderRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public OrderDetailForPayment getOrder(String orderSn) {
        OrderDetailDto dto = orderRestClient.get().uri("/internal/orders/{orderSn}", orderSn)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken()).retrieve()
                .body(OrderDetailDto.class);
        if (dto == null) {
            throw new IllegalStateException("tengan-order 回傳空白訂單資料: orderSn=" + orderSn);
        }
        String itemName = buildItemName(dto.items());
        return new OrderDetailForPayment(dto.orderSn(), dto.memberId(), dto.status(), dto.payAmount(), itemName);
    }

    @Override
    public void markPaid(String orderSn) {
        orderRestClient.put().uri("/internal/orders/{orderSn}/paid", orderSn)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken()).retrieve()
                .toBodilessEntity();
    }

    private String buildItemName(List<OrderDetailDto.OrderItemDto> items) {
        if (items == null || items.isEmpty()) {
            return "Tengan Mall 商品";
        }
        String first = items.get(0).skuName();
        return items.size() > 1 ? first + " 等" + items.size() + "件" : first;
    }
}
