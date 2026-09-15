package com.tengan.mall.product.domain.exception;

/** SKU 填值時引用的 standardValueId 跟指定的 attrId 不是同一個屬性——SpuCompositionAssembler 用。 */
public class SaleAttrStandardValueAttrMismatchException extends RuntimeException {

    public SaleAttrStandardValueAttrMismatchException(Long standardValueId, Long attrId) {
        super("standardValueId=" + standardValueId + " 不屬於 attrId=" + attrId);
    }
}
