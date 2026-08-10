package com.tengan.mall.order.infrastructure.cart;

import com.tengan.mall.order.application.port.CartPort;
import com.tengan.mall.order.application.port.CheckedCartItem;
import com.tengan.mall.order.infrastructure.cart.dto.CheckedItemDto;
import com.tengan.mall.order.infrastructure.cart.dto.RemoveItemsAfterOrderRequest;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class CartAdapter implements CartPort {

    private final RestClient cartRestClient;
    private final CartServiceTokenProvider tokenProvider;

    public CartAdapter(RestClient cartRestClient, CartServiceTokenProvider tokenProvider) {
        this.cartRestClient = cartRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<CheckedCartItem> getCheckedItems(Long memberId) {
        List<CheckedItemDto> dtos = cartRestClient.get()
                .uri("/internal/cart/{userId}/items", memberId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<CheckedItemDto>>() {
                });
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream().map(dto -> new CheckedCartItem(dto.skuId(), dto.count())).toList();
    }

    @Override
    public void removeItemsAfterOrder(Long memberId, List<Long> skuIds) {
        cartRestClient.method(org.springframework.http.HttpMethod.DELETE)
                .uri("/internal/cart/{userId}/items", memberId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .body(new RemoveItemsAfterOrderRequest(skuIds))
                .retrieve()
                .toBodilessEntity();
    }
}
