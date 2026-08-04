package com.tengan.mall.product.domain.model;

/**
 * 聚合根：銷售屬性樣板（例如顏色、容量），同一個 SPU 底下各 SKU 各自的值不同，驅動前台規格切換 UI
 * （實際填值存在 sku_sale_attr_value，這裡只是分類層級的樣板定義，確保同分類底下大家用同一個
 * attr_id/名稱，不會出現「顏色」「色彩」兩種同義詞把 tengan-search 的篩選聚合打散）。
 * 天生不分組——沒有 attrGroupId 欄位，這是跟 {@link BaseAttr} 拆成兩個型別後最直接的結構差異。
 */
public class SaleAttr {

    private Long id;
    private final Long categoryId;
    private String name;
    private boolean searchable;
    private int sort;

    private SaleAttr(Long id, Long categoryId, String name, boolean searchable, int sort) {
        this.id = id;
        this.categoryId = categoryId;
        this.name = name;
        this.searchable = searchable;
        this.sort = sort;
    }

    public static SaleAttr create(Long categoryId, String name, boolean searchable, int sort) {
        return new SaleAttr(null, categoryId, name, searchable, sort);
    }

    public static SaleAttr reconstitute(Long id, Long categoryId, String name, boolean searchable, int sort) {
        return new SaleAttr(id, categoryId, name, searchable, sort);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("SaleAttr 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void updateSearchable(boolean searchable) {
        this.searchable = searchable;
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

    public boolean isSearchable() {
        return searchable;
    }

    public int getSort() {
        return sort;
    }
}
