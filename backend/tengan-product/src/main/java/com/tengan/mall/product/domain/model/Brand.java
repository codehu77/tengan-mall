package com.tengan.mall.product.domain.model;

/** 聚合根：商品品牌。平面結構，沒有階層，不像 Category 需要 level/parentId。 */
public class Brand {

    private Long id;
    private String name;
    private String logo;
    private String descript;
    private String firstLetter;
    private int sort;
    private BrandStatus status;

    private Brand(Long id, String name, String logo, String descript, String firstLetter, int sort,
            BrandStatus status) {
        this.id = id;
        this.name = name;
        this.logo = logo;
        this.descript = descript;
        this.firstLetter = firstLetter;
        this.sort = sort;
        this.status = status;
    }

    public static Brand create(String name, String logo, String descript, String firstLetter, int sort) {
        return new Brand(null, name, logo, descript, firstLetter, sort, BrandStatus.SHOW);
    }

    public static Brand reconstitute(Long id, String name, String logo, String descript, String firstLetter,
            int sort, BrandStatus status) {
        return new Brand(id, name, logo, descript, firstLetter, sort, status);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Brand 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void rename(String name) {
        this.name = name;
    }

    public void updateLogo(String logo) {
        this.logo = logo;
    }

    public void updateDescript(String descript) {
        this.descript = descript;
    }

    public void updateFirstLetter(String firstLetter) {
        this.firstLetter = firstLetter;
    }

    public void updateSort(int sort) {
        this.sort = sort;
    }

    public void show() {
        this.status = BrandStatus.SHOW;
    }

    public void hide() {
        this.status = BrandStatus.HIDDEN;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getLogo() {
        return logo;
    }

    public String getDescript() {
        return descript;
    }

    public String getFirstLetter() {
        return firstLetter;
    }

    public int getSort() {
        return sort;
    }

    public BrandStatus getStatus() {
        return status;
    }
}
