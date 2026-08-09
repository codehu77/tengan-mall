package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.ProductSkuPort;
import com.tengan.mall.admin.application.port.SkuItem;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class ProductSkuAdapter implements ProductSkuPort {

    private static final String BASE_PATH = "/internal/products/skus";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductSkuAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<SkuItem> batchGet(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return List.of();
        }
        String uri = UriComponentsBuilder.fromPath(BASE_PATH)
                .queryParam("ids", skuIds.toArray())
                .build().toUriString();
        SkuItem[] items = productRestClient.get()
                .uri(uri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SkuItem[].class);
        return items == null ? List.of() : List.of(items);
    }
}
