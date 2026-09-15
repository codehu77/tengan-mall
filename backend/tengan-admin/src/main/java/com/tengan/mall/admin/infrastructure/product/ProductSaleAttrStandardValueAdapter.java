package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.CreateSaleAttrStandardValuePayload;
import com.tengan.mall.admin.application.port.ProductSaleAttrStandardValuePort;
import com.tengan.mall.admin.application.port.SaleAttrStandardValueItem;
import com.tengan.mall.admin.application.port.UpdateSaleAttrStandardValuePayload;
import com.tengan.mall.admin.infrastructure.product.dto.IdEnvelope;
import com.tengan.mall.admin.infrastructure.product.dto.SaleAttrStandardValueListEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductSaleAttrStandardValueAdapter implements ProductSaleAttrStandardValuePort {

    private static final String ATTR_PATH = "/internal/products/sale-attrs";
    private static final String STANDARD_VALUE_PATH = "/internal/products/sale-attr-standard-values";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductSaleAttrStandardValueAdapter(RestClient productRestClient,
            ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<SaleAttrStandardValueItem> listByAttr(Long attrId) {
        SaleAttrStandardValueListEnvelope envelope = productRestClient.get()
                .uri(ATTR_PATH + "/{attrId}/standard-values", attrId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SaleAttrStandardValueListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public List<SaleAttrStandardValueItem> listByCategory(Long categoryId) {
        SaleAttrStandardValueListEnvelope envelope = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path(ATTR_PATH + "/standard-values")
                        .queryParam("categoryId", categoryId).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SaleAttrStandardValueListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long create(Long attrId, CreateSaleAttrStandardValuePayload payload, String operatorToken) {
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
    public void update(Long id, UpdateSaleAttrStandardValuePayload payload, String operatorToken) {
        productRestClient.put()
                .uri(STANDARD_VALUE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }
}
