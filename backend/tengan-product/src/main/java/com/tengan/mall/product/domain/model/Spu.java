package com.tengan.mall.product.domain.model;

import com.tengan.mall.product.domain.exception.SpuHasNoSkuException;
import com.tengan.mall.product.domain.exception.SpuNotOnShelfException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根：商品(SPU)，Sku 是內部 child entity，不是獨立聚合根——上架(publish)有「底下至少要有一顆
 * Sku」這個不變條件，需要交易一致性保護（同一個 transaction 存檔，不會出現檢查完、存檔前 Sku 被刪光
 * 的時間窗口），這是判斷該合併成一個聚合根的訊號。前台高頻讀取（GET /skus/{skuId}）不經過這個聚合根，
 * 走獨立的 SkuDetailPort 直接查，所以合併付出的代價只在低頻的後台寫入路徑。
 */
public class Spu {

    private Long id;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String description;
    private String mainImage;
    private SpuStatus status;
    private final List<Sku> skus = new ArrayList<>();
    private final List<SpuBaseAttrValue> attrValues = new ArrayList<>();
    private final List<SpuImage> images = new ArrayList<>();

    private Spu(Long id, Long categoryId, Long brandId, String name, String description, String mainImage,
            SpuStatus status) {
        this.id = id;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.mainImage = mainImage;
        this.status = status;
    }

    public static Spu create(Long categoryId, Long brandId, String name, String description, String mainImage) {
        return new Spu(null, categoryId, brandId, name, description, mainImage, SpuStatus.NEW);
    }

    public static Spu reconstitute(Long id, Long categoryId, Long brandId, String name, String description,
            String mainImage, SpuStatus status, List<Sku> skus, List<SpuBaseAttrValue> attrValues,
            List<SpuImage> images) {
        Spu spu = new Spu(id, categoryId, brandId, name, description, mainImage, status);
        spu.skus.addAll(skus);
        spu.attrValues.addAll(attrValues);
        spu.images.addAll(images);
        return spu;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Spu 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void updateBasicInfo(Long categoryId, Long brandId, String name, String description, String mainImage) {
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.mainImage = mainImage;
    }

    /** 整批替換底下的 Sku 集合，差異寫入（新增 insert、既有 update、被移除的 delete）交給 Repository 處理。 */
    public void replaceSkus(List<Sku> newSkus) {
        this.skus.clear();
        this.skus.addAll(newSkus);
    }

    public void replaceAttrValues(List<SpuBaseAttrValue> newAttrValues) {
        this.attrValues.clear();
        this.attrValues.addAll(newAttrValues);
    }

    public void replaceImages(List<SpuImage> newImages) {
        this.images.clear();
        this.images.addAll(newImages);
    }

    public void publish() {
        if (skus.isEmpty()) {
            throw new SpuHasNoSkuException(id);
        }
        this.status = SpuStatus.ON_SHELF;
    }

    public void unlist() {
        if (status != SpuStatus.ON_SHELF) {
            throw new SpuNotOnShelfException(id);
        }
        this.status = SpuStatus.OFF_SHELF;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMainImage() {
        return mainImage;
    }

    public SpuStatus getStatus() {
        return status;
    }

    public List<Sku> getSkus() {
        return Collections.unmodifiableList(skus);
    }

    public List<SpuBaseAttrValue> getAttrValues() {
        return Collections.unmodifiableList(attrValues);
    }

    public List<SpuImage> getImages() {
        return Collections.unmodifiableList(images);
    }
}
