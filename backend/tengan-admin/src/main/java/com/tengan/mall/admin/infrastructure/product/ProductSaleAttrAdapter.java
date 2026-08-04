package com.tengan.mall.admin.infrastructure.product;

import com.tengan.mall.admin.application.port.CreateSaleAttrPayload;
import com.tengan.mall.admin.application.port.ProductSaleAttrPort;
import com.tengan.mall.admin.application.port.SaleAttrItem;
import com.tengan.mall.admin.application.port.UpdateSaleAttrPayload;
import com.tengan.mall.admin.infrastructure.product.dto.IdEnvelope;
import com.tengan.mall.admin.infrastructure.product.dto.SaleAttrListEnvelope;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductSaleAttrAdapter implements ProductSaleAttrPort {

    private static final String BASE_PATH = "/internal/products/sale-attrs";

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductSaleAttrAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<SaleAttrItem> listSaleAttrs(Long categoryId) {
        SaleAttrListEnvelope envelope = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path(BASE_PATH).queryParam("categoryId", categoryId).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SaleAttrListEnvelope.class);
        return envelope == null ? List.of() : envelope.items();
    }

    @Override
    public Long createSaleAttr(CreateSaleAttrPayload payload, String operatorToken) {
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
    public void updateSaleAttr(Long id, UpdateSaleAttrPayload payload, String operatorToken) {
        productRestClient.put()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void deleteSaleAttr(Long id, String operatorToken) {
        productRestClient.delete()
                .uri(BASE_PATH + "/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .header("X-Identity-Assertion", "Bearer " + operatorToken)
                .retrieve()
                .toBodilessEntity();
    }
}
