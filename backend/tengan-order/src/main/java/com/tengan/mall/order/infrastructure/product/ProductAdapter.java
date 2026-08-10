package com.tengan.mall.order.infrastructure.product;

import com.tengan.mall.order.application.port.PricedSkuInfo;
import com.tengan.mall.order.application.port.ProductPort;
import com.tengan.mall.order.infrastructure.product.dto.SkuDetailDto;
import java.util.List;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductAdapter implements ProductPort {

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<PricedSkuInfo> batchPrice(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return List.of();
        }
        List<SkuDetailDto> dtos = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/products/skus").queryParam("ids", skuIds).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(new ParameterizedTypeReference<List<SkuDetailDto>>() {
                });
        if (dtos == null) {
            return List.of();
        }
        return dtos.stream()
                .map(dto -> new PricedSkuInfo(dto.id(), dto.spuId(), dto.name(), dto.price(), dto.mainImage()))
                .toList();
    }
}
