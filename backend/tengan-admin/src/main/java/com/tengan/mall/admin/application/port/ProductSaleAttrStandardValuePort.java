package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-product 的標準聚合值 internal 端點，跟 {@link ProductSaleAttrPort} 同樣的純代理原則。 */
public interface ProductSaleAttrStandardValuePort {

    List<SaleAttrStandardValueItem> listByAttr(Long attrId);

    List<SaleAttrStandardValueItem> listByCategory(Long categoryId);

    Long create(Long attrId, CreateSaleAttrStandardValuePayload payload, String operatorToken);

    void update(Long id, UpdateSaleAttrStandardValuePayload payload, String operatorToken);
}
