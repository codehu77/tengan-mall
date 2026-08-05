package com.tengan.mall.search.infrastructure.product;

import com.tengan.mall.search.application.ProductCatalogPage;
import com.tengan.mall.search.application.ProductCatalogPort;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductCatalogAdapter implements ProductCatalogPort {

    private static final String PATH = "/internal/products/spus/search-export";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductCatalogAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public ProductCatalogPage fetchPage(int pageNum, int pageSize) {
        ProductCatalogPage page = productRestClient.get()
                .uri(PATH + "?pageNum={pageNum}&pageSize={pageSize}", pageNum, pageSize)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(ProductCatalogPage.class);
        return page == null ? new ProductCatalogPage(java.util.List.of(), false) : page;
    }
}
