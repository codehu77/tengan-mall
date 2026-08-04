package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.BrandItem;
import com.tengan.mall.admin.application.port.CreateBrandPayload;
import com.tengan.mall.admin.application.port.ProductBrandPort;
import com.tengan.mall.admin.application.port.UpdateBrandPayload;
import com.tengan.mall.admin.infrastructure.product.dto.BrandListEnvelope;
import com.tengan.mall.admin.infrastructure.product.dto.IdEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductBrandAdapter implements ProductBrandPort {

    private static final String BASE_PATH = "/internal/products/brands";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductBrandAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<BrandItem> listBrands() {
        BrandListEnvelope envelope = productRestClient.get()
                .uri(BASE_PATH)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(BrandListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long createBrand(CreateBrandPayload payload, String operatorToken) {
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
    public void updateBrand(Long id, UpdateBrandPayload payload, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteBrand(Long id, String operatorToken) {
        productRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void showBrand(Long id, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}/show", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void hideBrand(Long id, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}/hide", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }
}
