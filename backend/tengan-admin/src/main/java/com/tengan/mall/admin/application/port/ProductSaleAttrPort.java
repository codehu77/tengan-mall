package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-product 的銷售屬性 internal 端點，跟 {@link ProductCategoryPort} 同樣的純代理原則。 */
public interface ProductSaleAttrPort {

    List<SaleAttrItem> listSaleAttrs(Long categoryId);

    Long createSaleAttr(CreateSaleAttrPayload payload, String operatorToken);

    void updateSaleAttr(Long id, UpdateSaleAttrPayload payload, String operatorToken);

    void deleteSaleAttr(Long id, String operatorToken);
}
