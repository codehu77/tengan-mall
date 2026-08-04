package com.tengan.mall.product.domain.model;

/**
 * 聚合根：規格參數（例如螢幕尺寸、晶片），同一個 SPU 底下所有 SKU 共用同一份值
 * （實際填值存在 spu_base_attr_value，這裡只是樣板定義）。
 * 一定要歸屬一個 {@link BaseAttrGroup} 做分組顯示——跟 SaleAttr 的關鍵差異，這條規則現在由
 * 「attrGroupId 是 NOT NULL 欄位」這個結構本身保證，不需要再靠 domain 建構子檢查
 * （比起舊版 Attr 用 attrType 分支判斷，這是拆成兩個型別後拿到的實際好處：不合法狀態直接
 * 沒有辦法被建構出來）。
 */
public class BaseAttr {

    private Long id;
    private final Long categoryId;
    private Long attrGroupId;
    private String name;
    private boolean searchable;
    private int sort;

    private BaseAttr(Long id, Long categoryId, Long attrGroupId, String name, boolean searchable, int sort) {
        this.id = id;
        this.categoryId = categoryId;
        this.attrGroupId = attrGroupId;
        this.name = name;
        this.searchable = searchable;
        this.sort = sort;
    }

    public static BaseAttr create(Long categoryId, Long attrGroupId, String name, boolean searchable, int sort) {
        return new BaseAttr(null, categoryId, attrGroupId, name, searchable, sort);
    }

    public static BaseAttr reconstitute(Long id, Long categoryId, Long attrGroupId, String name, boolean searchable,
            int sort) {
        return new BaseAttr(id, categoryId, attrGroupId, name, searchable, sort);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("BaseAttr 已經有 id，不可重複指派: " + this.id);
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

    /** 可以改掛到同分類底下的另一個 BaseAttrGroup（跨分類要不要合法由 application 層驗證）。 */
    public void moveToAttrGroup(Long attrGroupId) {
        if (attrGroupId == null) {
            throw new IllegalArgumentException("BaseAttr 必須指定 attrGroupId");
        }
        this.attrGroupId = attrGroupId;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getAttrGroupId() {
        return attrGroupId;
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
