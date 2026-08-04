package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.BaseAttrItem;
import com.tengan.mall.admin.application.port.CreateBaseAttrPayload;
import com.tengan.mall.admin.application.port.ProductBaseAttrPort;
import com.tengan.mall.admin.application.port.UpdateBaseAttrPayload;
import com.tengan.mall.admin.infrastructure.product.dto.BaseAttrListEnvelope;
import com.tengan.mall.admin.infrastructure.product.dto.IdEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductBaseAttrAdapter implements ProductBaseAttrPort {

    private static final String BASE_PATH = "/internal/products/base-attrs";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductBaseAttrAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<BaseAttrItem> listBaseAttrs(Long categoryId) {
        BaseAttrListEnvelope envelope = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("categoryId", categoryId).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(BaseAttrListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long createBaseAttr(CreateBaseAttrPayload payload, String operatorToken) {
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
    public void updateBaseAttr(Long id, UpdateBaseAttrPayload payload, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteBaseAttr(Long id, String operatorToken) {
        productRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }
}
