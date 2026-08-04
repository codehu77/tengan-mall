package com.tengan.mall.product.domain.exception;

/** attrGroupId 指向的 BaseAttrGroup 跟 BaseAttr 本身的 categoryId 不是同一個分類——資料完整性錯誤。 */
public class BaseAttrGroupCategoryMismatchException extends RuntimeException {

    public BaseAttrGroupCategoryMismatchException(Long attrGroupId, Long categoryId) {
        super("attrGroupId=" + attrGroupId + " 不屬於 categoryId=" + categoryId);
    }
}
