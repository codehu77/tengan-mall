package com.tengan.mall.product.domain.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Spu 聚合根內部的 child entity，沒有自己的 Repository——要改必須先拿到整個 Spu，
 * 透過 Spu.replaceSkus() 整批替換，由 SpuRepositoryImpl 負責跟既有資料表列做差異寫入。
 * saleCount 這次不做遞增邏輯（要等 tengan-order 訂單完成才能觸發），建立時固定 0。
 */
public class Sku {

    private Long id;
    private String name;
    private BigDecimal price;
    private String mainImage;
    private final int saleCount;
    private int sort;
    private final List<SkuImage> images = new ArrayList<>();
    private final List<SkuSaleAttrValue> saleAttrValues = new ArrayList<>();

    private Sku(Long id, String name, BigDecimal price, String mainImage, int saleCount, int sort) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.mainImage = mainImage;
        this.saleCount = saleCount;
        this.sort = sort;
    }

    public static Sku create(String name, BigDecimal price, String mainImage, int sort, List<SkuImage> images,
            List<SkuSaleAttrValue> saleAttrValues) {
        Sku sku = new Sku(null, name, price, mainImage, 0, sort);
        sku.images.addAll(images);
        sku.saleAttrValues.addAll(saleAttrValues);
        return sku;
    }

    public static Sku reconstitute(Long id, String name, BigDecimal price, String mainImage, int saleCount, int sort,
            List<SkuImage> images, List<SkuSaleAttrValue> saleAttrValues) {
        Sku sku = new Sku(id, name, price, mainImage, saleCount, sort);
        sku.images.addAll(images);
        sku.saleAttrValues.addAll(saleAttrValues);
        return sku;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Sku 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getMainImage() {
        return mainImage;
    }

    public int getSaleCount() {
        return saleCount;
    }

    public int getSort() {
        return sort;
    }

    public List<SkuImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    public List<SkuSaleAttrValue> getSaleAttrValues() {
        return Collections.unmodifiableList(saleAttrValues);
    }
}
