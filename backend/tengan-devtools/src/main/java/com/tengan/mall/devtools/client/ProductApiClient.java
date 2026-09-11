package com.tengan.mall.devtools.client;

import com.tengan.mall.devtools.client.dto.BaseAttrList;
import com.tengan.mall.devtools.client.dto.BrandList;
import com.tengan.mall.devtools.client.dto.CategoryTree;
import com.tengan.mall.devtools.client.dto.CreateSpuRequest;
import com.tengan.mall.devtools.client.dto.CreateSpuResponse;
import com.tengan.mall.devtools.client.dto.SaleAttrList;
import com.tengan.mall.devtools.client.dto.SpuSkuList;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** 呼叫 tengan-product 的 internal API：分類/品牌/屬性唯讀查詢（給前端下拉選單用）+ 建立 SPU。 */
@Component
public class ProductApiClient {

    private static final String REGISTRATION_ID = "tengan-product";

    private final RestClient productRestClient;
    private final ServiceTokenProvider tokenProvider;

    public ProductApiClient(RestClient productRestClient, ServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    public CategoryTree categories() {
        return productRestClient.get()
                .uri("/internal/products/categories")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .body(CategoryTree.class);
    }

    public BrandList brands() {
        return productRestClient.get()
                .uri("/internal/products/brands")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .body(BrandList.class);
    }

    public BaseAttrList baseAttrs(Long categoryId) {
        return productRestClient.get()
                .uri("/internal/products/base-attrs?categoryId={categoryId}", categoryId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .body(BaseAttrList.class);
    }

    public SaleAttrList saleAttrs(Long categoryId) {
        return productRestClient.get()
                .uri("/internal/products/sale-attrs?categoryId={categoryId}", categoryId)
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .body(SaleAttrList.class);
    }

    public CreateSpuResponse createSpu(CreateSpuRequest request) {
        return productRestClient.post()
                .uri("/internal/products/spus")
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .body(request)
                .retrieve()
                .body(CreateSpuResponse.class);
    }

    /** 建完 SPU 後拿實際落地的 SKU id 清單——CreateSpuResponse 只回 SPU id，SKU id 是 DB 自動產生的。 */
    public SpuSkuList getSpu(Long id) {
        return productRestClient.get()
                .uri("/internal/products/spus/{id}", id)
                .header(HttpHeaders.AUTHORIZATION, bearerToken())
                .retrieve()
                .body(SpuSkuList.class);
    }

    private String bearerToken() {
        return "Bearer " + tokenProvider.getAccessToken(REGISTRATION_ID);
    }
}
