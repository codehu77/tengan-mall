package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.ProductAttrSummaryPort;
import com.tengan.mall.admin.infrastructure.product.dto.CategoriesWithAttrsEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductAttrSummaryAdapter implements ProductAttrSummaryPort {

    private static final String BASE_PATH = "/internal/products/categories/with-attrs";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductAttrSummaryAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<Long> listCategoriesWithAttrs() {
        CategoriesWithAttrsEnvelope envelope = productRestClient.get()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(CategoriesWithAttrsEnvelope.class);
        return envelope == null ? List.of() : envelope.categoryIds();
    }
}
