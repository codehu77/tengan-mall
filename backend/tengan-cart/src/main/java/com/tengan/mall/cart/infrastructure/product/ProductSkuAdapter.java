package com.tengan.mall.cart.infrastructure.product;

import com.tengan.mall.cart.application.cart.ProductSkuInfo;
import com.tengan.mall.cart.application.cart.ProductSkuPort;
import com.tengan.mall.cart.infrastructure.product.dto.SkuDetailDto;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductSkuAdapter implements ProductSkuPort {

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductSkuAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<ProductSkuInfo> findByIds(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return List.of();
        }
        List<SkuDetailDto> dtos = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/products/skus")
                        .queryParam("ids", skuIds)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<SkuDetailDto>>() {
                });
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream()
                .map(dto -> new ProductSkuInfo(dto.id(), dto.spuId(), dto.name(), dto.price(), dto.mainImage()))
                .toList();
    }
}
