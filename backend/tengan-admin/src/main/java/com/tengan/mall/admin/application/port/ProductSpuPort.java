package com.tengan.mall.admin.application.port;

/**
 * 呼叫 tengan-product 的 SPU/SKU internal 端點，跟 {@link ProductCategoryPort} 同樣的純代理原則——
 * publish/unlist 的不變條件（至少一顆 Sku、必須是上架中才能下架）真相都在 tengan-product，這裡只轉發。
 */
public interface ProductSpuPort {

    SpuSearchResult searchSpus(SpuSearchParams params);

    SpuDetailItem getSpu(Long id);

    Long createSpu(CreateSpuPayload payload, String operatorToken);

    void updateSpu(Long id, UpdateSpuPayload payload, String operatorToken);

    void publishSpu(Long id, String operatorToken);

    void unlistSpu(Long id, String operatorToken);

    void deleteSpu(Long id, String operatorToken);
}
