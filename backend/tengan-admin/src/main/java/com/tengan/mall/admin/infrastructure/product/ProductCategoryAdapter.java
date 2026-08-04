package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.CategoryTreeItem;
import com.tengan.mall.admin.application.port.CreateCategoryPayload;
import com.tengan.mall.admin.application.port.ProductCategoryPort;
import com.tengan.mall.admin.application.port.UpdateCategoryPayload;
import com.tengan.mall.admin.infrastructure.product.dto.CategoryTreeEnvelope;
import com.tengan.mall.admin.infrastructure.product.dto.IdEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductCategoryAdapter implements ProductCategoryPort {

    private static final String BASE_PATH = "/internal/products/categories";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductCategoryAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<CategoryTreeItem> listCategories() {
        CategoryTreeEnvelope envelope = productRestClient.get()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(CategoryTreeEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long createCategory(CreateCategoryPayload payload, String operatorToken) {
        IdEnvelope envelope = productRestClient.post()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .body(IdEnvelope.class);
        return envelope.id();
    }

    @Override
    public void updateCategory(Long id, UpdateCategoryPayload payload, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteCategory(Long id, String operatorToken) {
        productRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void showCategory(Long id, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}/show", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void hideCategory(Long id, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}/hide", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }
}
