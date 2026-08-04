package com.tengan.mall.product.domain.exception;

/** AttrGroup/Attr 只能綁第三層（葉節點）分類——第一/二層是導覽分組節點，沒有唯一的屬性樣板可言。 */
public class CategoryNotLeafException extends RuntimeException {

    public CategoryNotLeafException(Long categoryId) {
        super("分類必須是第三層（葉節點）才能綁定屬性樣板: categoryId=" + categoryId);
    }
}
