package com.tengan.mall.product.domain.model;

/**
 * 聚合根：規格參數分組（例如「主體」「顯示螢幕」），只能綁在第三層分類底下——
 * 分類第一/二層是導覽分組節點，只有第三層才是 SPU 真正掛的葉節點，屬性樣板只對葉節點有意義
 * （level 是否為 3 的驗證放在 application 層，因為要跨聚合根查 CategoryRepository）。
 * 只服務 BaseAttr——銷售屬性（SaleAttr）天生不分組，不會出現在這裡。
 */
public class BaseAttrGroup {

    private Long id;
    private final Long categoryId;
    private String name;
    private int sort;

    private BaseAttrGroup(Long id, Long categoryId, String name, int sort) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.sort = sort;
    }

    public static BaseAttrGroup create(Long categoryId, String name, int sort) {
        return new BaseAttrGroup(null, categoryId, name, sort);
    }

    public static BaseAttrGroup reconstitute(Long id, Long categoryId, String name, int sort) {
        return new BaseAttrGroup(id, categoryId, name, sort);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("BaseAttrGroup 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void updateSort(int sort) {
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public int getSort() {
        return sort;
    }
}
