package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.BaseAttrGroupItem;
import com.tengan.mall.admin.application.port.CreateBaseAttrGroupPayload;
import com.tengan.mall.admin.application.port.ProductBaseAttrGroupPort;
import com.tengan.mall.admin.application.port.UpdateBaseAttrGroupPayload;
import com.tengan.mall.admin.infrastructure.product.dto.BaseAttrGroupListEnvelope;
import com.tengan.mall.admin.infrastructure.product.dto.IdEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductBaseAttrGroupAdapter implements ProductBaseAttrGroupPort {

    private static final String BASE_PATH = "/internal/products/base-attr-groups";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductBaseAttrGroupAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<BaseAttrGroupItem> listBaseAttrGroups(Long categoryId) {
        BaseAttrGroupListEnvelope envelope = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("categoryId", categoryId).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(BaseAttrGroupListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long createBaseAttrGroup(CreateBaseAttrGroupPayload payload, String operatorToken) {
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
    public void updateBaseAttrGroup(Long id, UpdateBaseAttrGroupPayload payload, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteBaseAttrGroup(Long id, String operatorToken) {
        productRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }
}
