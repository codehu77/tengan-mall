package com.tengan.mall.product.domain.model;

/**
 * 聚合根：商品分類，最多三層樹狀結構（level 1~3），parentId=0 代表頂層。
 * 不變條件：level 由 parentId 對應的父節點推算，不接受呼叫端直接指定（見 CreateCategoryService）。
 */
public class Category {

    private Long id;
    private final Long parentId;
    private String name;
    private String icon;
    private int sort;
    private final int level;
    private CategoryStatus status;

    private Category(Long id, Long parentId, String name, String icon, int sort, int level,
            CategoryStatus status) {
        this.id = id;
        this.parentId = parentId;
        this.name = name;
        this.icon = icon;
        this.sort = sort;
        this.level = level;
        this.status = status;
    }

    public static Category create(Long parentId, String name, String icon, int sort, int level) {
        return new Category(null, parentId, name, icon, sort, level, CategoryStatus.VISIBLE);
    }

    public static Category reconstitute(Long id, Long parentId, String name, String icon, int sort, int level,
            CategoryStatus status) {
        return new Category(id, parentId, name, icon, sort, level, status);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Category 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void updateIcon(String icon) {
        this.icon = icon;
    }

    public void updateSort(int sort) {
        this.sort = sort;
    }

    public void show() {
        this.status = CategoryStatus.VISIBLE;
    }

    public void hide() {
        this.status = CategoryStatus.HIDDEN;
    }

    public Long getId() {
        return id;
    }

    public Long getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public int getSort() {
        return sort;
    }

    public int getLevel() {
        return level;
    }

    public CategoryStatus getStatus() {
        return status;
    }
}
