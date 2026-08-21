package com.tengan.mall.seckill.infrastructure.product;

import com.tengan.mall.seckill.application.port.ProductPort;
import com.tengan.mall.seckill.application.port.SkuInfo;
import java.util.List;
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
    public List<SkuInfo> batchGet(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return List.of();
        }
        SkuDetailDto[] response = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/products/skus").queryParam("ids", skuIds).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SkuDetailDto[].class);
        if (response == null) {
            return List.of();
        }
        return List.of(response).stream()
                .map(dto -> new SkuInfo(dto.id(), dto.spuId(), dto.name(), dto.mainImage(), dto.price()))
                .toList();
    }
}
