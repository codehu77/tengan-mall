package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.CreateSpuPayload;
import com.tengan.mall.admin.application.port.ProductSpuPort;
import com.tengan.mall.admin.application.port.SpuDetailItem;
import com.tengan.mall.admin.application.port.SpuSearchParams;
import com.tengan.mall.admin.application.port.SpuSearchResult;
import com.tengan.mall.admin.application.port.UpdateSpuPayload;
import com.tengan.mall.admin.infrastructure.product.dto.IdEnvelope;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductSpuAdapter implements ProductSpuPort {

    private static final String BASE_PATH = "/internal/products/spus";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductSpuAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public SpuSearchResult searchSpus(SpuSearchParams params) {
        return productRestClient.get()
                .uri(uriBuilder -> {
                    uriBuilder.path(BASE_PATH)
                            .queryParam("pageNum", params.pageNum())
                            .queryParam("pageSize", params.pageSize());
                    if (params.categoryId() != null) {
                        uriBuilder.queryParam("categoryId", params.categoryId());
                    }
                    if (params.brandId() != null) {
                        uriBuilder.queryParam("brandId", params.brandId());
                    }
                    if (params.name() != null && !params.name().isBlank()) {
                        uriBuilder.queryParam("name", params.name());
                    }
                    if (params.status() != null) {
                        uriBuilder.queryParam("status", params.status());
                    }
                    return uriBuilder.build();
                })
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SpuSearchResult.class);
    }

    @Override
    public SpuDetailItem getSpu(Long id) {
        return productRestClient.get()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SpuDetailItem.class);
    }

    @Override
    public Long createSpu(CreateSpuPayload payload, String operatorToken) {
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
    public void updateSpu(Long id, UpdateSpuPayload payload, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void publishSpu(Long id, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}/publish", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void unlistSpu(Long id, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}/unlist", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteSpu(Long id, String operatorToken) {
        productRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public Long duplicateSpu(Long id, String operatorToken) {
        IdEnvelope envelope = productRestClient.post()
                .uri(BASE_PATH + "/{id}/duplicate", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .body(IdEnvelope.class);
        return envelope.id();
    }
}
