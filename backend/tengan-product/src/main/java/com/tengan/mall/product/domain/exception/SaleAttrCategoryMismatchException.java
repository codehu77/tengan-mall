package com.tengan.mall.product.domain.exception;

/** Sku 引用的 SaleAttr 跟 Spu 本身的 categoryId 不是同一個分類——SpuCompositionAssembler 用。 */
public class SaleAttrCategoryMismatchException extends RuntimeException {

    public SaleAttrCategoryMismatchException(Long attrId, Long categoryId) {
        super("attrId=" + attrId + " 不屬於 categoryId=" + categoryId);
    }
}
