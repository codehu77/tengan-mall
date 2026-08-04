package com.tengan.mall.product.domain.exception;

/** 分類樹最深只到第三層——AttrGroup/Attr 只能綁第三層分類，第四層以後綁不到任何屬性樣板。 */
public class CategoryLevelLimitExceededException extends RuntimeException {

    public CategoryLevelLimitExceededException(Long parentId) {
        super("分類最多三層，parentId=" + parentId + " 已經是第三層，不能再新增子分類");
    }
}
