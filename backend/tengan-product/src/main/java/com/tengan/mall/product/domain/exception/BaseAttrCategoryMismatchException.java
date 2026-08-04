package com.tengan.mall.product.domain.exception;

/** Spu 引用的 BaseAttr 跟 Spu 本身的 categoryId 不是同一個分類——SpuCompositionAssembler 用。 */
public class BaseAttrCategoryMismatchException extends RuntimeException {

    public BaseAttrCategoryMismatchException(Long attrId, Long categoryId) {
        super("attrId=" + attrId + " 不屬於 categoryId=" + categoryId);
    }
}
