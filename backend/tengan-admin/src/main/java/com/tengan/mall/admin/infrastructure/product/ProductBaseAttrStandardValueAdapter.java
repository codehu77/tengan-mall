package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.BaseAttrStandardValueItem;
import com.tengan.mall.admin.application.port.CreateBaseAttrStandardValuePayload;
import com.tengan.mall.admin.application.port.ProductBaseAttrStandardValuePort;
import com.tengan.mall.admin.application.port.UpdateBaseAttrStandardValuePayload;
import com.tengan.mall.admin.infrastructure.product.dto.BaseAttrStandardValueListEnvelope;
import com.tengan.mall.admin.infrastructure.product.dto.IdEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductBaseAttrStandardValueAdapter implements ProductBaseAttrStandardValuePort {

    private static final String ATTR_PATH = "/internal/products/base-attrs";
    private static final String STANDARD_VALUE_PATH = "/internal/products/base-attr-standard-values";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductBaseAttrStandardValueAdapter(RestClient productRestClient,
            ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<BaseAttrStandardValueItem> listByAttr(Long attrId) {
        BaseAttrStandardValueListEnvelope envelope = productRestClient.get()
                .uri(ATTR_PATH + "/{attrId}/standard-values", attrId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(BaseAttrStandardValueListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public List<BaseAttrStandardValueItem> listByCategory(Long categoryId) {
        BaseAttrStandardValueListEnvelope envelope = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path(ATTR_PATH + "/standard-values")
                        .queryParam("categoryId", categoryId).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(BaseAttrStandardValueListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long create(Long attrId, CreateBaseAttrStandardValuePayload payload, String operatorToken) {
        IdEnvelope envelope = productRestClient.post()
                .uri(ATTR_PATH + "/{attrId}/standard-values", attrId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .body(IdEnvelope.class);
        return envelope.id();
    }

    @Override
    public void update(Long id, UpdateBaseAttrStandardValuePayload payload, String operatorToken) {
        productRestClient.put()
                .uri(STANDARD_VALUE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
