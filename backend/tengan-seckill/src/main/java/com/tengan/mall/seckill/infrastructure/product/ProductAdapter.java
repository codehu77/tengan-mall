package com.tengan.mall.seckill.infrastructure.product;

import com.tengan.mall.seckill.application.port.ProductPort;
import com.tengan.mall.seckill.application.port.SkuInfo;
import com.tengan.mall.seckill.application.port.SpuInfo;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class ProductAdapter implements ProductPort {

    private final RestClient productRestClient;
    private final ProductServiceTokenProvider tokenProvider;

    public ProductAdapter(RestClient productRestClient, ProductServiceTokenProvider tokenProvider) {
        this.productRestClient = productRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public List<SkuInfo> batchGet(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return List.of();
        }
        SkuDetailDto[] response = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/products/skus").queryParam("ids", skuIds).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SkuDetailDto[].class);
        if (response == null) {
            return List.of();
        }
        return List.of(response).stream()
                .map(dto -> new SkuInfo(dto.id(), dto.spuId(), dto.name(), dto.mainImage(), dto.price(),
                        variantLabel(dto)))
                .toList();
    }

    @Override
    public List<SpuInfo> batchGetSpu(List<Long> spuIds) {
        if (spuIds.isEmpty()) {
            return List.of();
        }
        SpuSummaryDto[] response = productRestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/internal/products/spus/batch").queryParam("ids", spuIds).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .retrieve()
                .body(SpuSummaryDto[].class);
        if (response == null) {
            return List.of();
        }
        return List.of(response).stream().map(dto -> new SpuInfo(dto.id(), dto.name(), dto.mainImage())).toList();
    }

    /** 用 saleAttrValues 的 attrValue 拼組規格標籤（例如「黑色/256G」），沒有屬性值時退回 sku 名稱。 */
    private String variantLabel(SkuDetailDto dto) {
        if (dto.saleAttrValues() == null || dto.saleAttrValues().isEmpty()) {
            return dto.name();
        }
        return dto.saleAttrValues().stream().map(SkuSaleAttrValueDto::attrValue)
                .reduce((a, b) -> a + "/" + b).orElse(dto.name());
    }
}
