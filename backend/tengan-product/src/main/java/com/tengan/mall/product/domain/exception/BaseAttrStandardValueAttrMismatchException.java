package com.tengan.mall.product.domain.exception;

/** SPU 填值時引用的 standardValueId 跟指定的 attrId 不是同一個屬性——SpuCompositionAssembler 用。 */
public class BaseAttrStandardValueAttrMismatchException extends RuntimeException {

    public BaseAttrStandardValueAttrMismatchException(Long standardValueId, Long attrId) {
        super("standardValueId=" + standardValueId + " 不屬於 attrId=" + attrId);
    }
}
