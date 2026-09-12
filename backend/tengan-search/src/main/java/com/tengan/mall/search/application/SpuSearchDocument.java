package com.tengan.mall.search.application;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 一個 SPU = 一份 ES 文件——minPrice/maxPrice 是索引階段就算好的彙總值，列表卡片直接讀這兩個欄位
 * 顯示「NT$ X 起」，不需要在查詢當下再摺疊/挑代表值。skus 是 nested 陣列，供規格層級（顏色/容量等
 * 銷售屬性）精準篩選，每個 skus 元素的 saleAttrs 又是一層 nested（nested-in-nested）。這份文件是
 * tengan-product 组好的「厚事件」直接落地，tengan-search 不回頭查任何其他服務。
 *
 * <p>spuName/skuName 用 ES 內建 standard 分詞器——沒有裝 IK/smartcn 中文分詞外掛，demo 情境下
 * 逐字切已經堪用，之後真的要精準中文分詞才需要另外處理 ES image。</p>
 */
@Document(indexName = "spu_search")
public class SpuSearchDocument {

    @Id
    private Long spuId;

    @Field(type = FieldType.Text)
    private String spuName;

    @Field(type = FieldType.Keyword)
    private String spuMainImage;

    @Field(type = FieldType.Double)
    private Double minPrice;

    @Field(type = FieldType.Double)
    private Double maxPrice;

    @Field(type = FieldType.Integer)
    private Integer saleCount;

    @Field(type = FieldType.Long)
    private Long brandId;

    @Field(type = FieldType.Keyword)
    private String brandName;

    @Field(type = FieldType.Long)
    private Long catalog1Id;

    @Field(type = FieldType.Keyword)
    private String catalog1Name;

    @Field(type = FieldType.Long)
    private Long catalog2Id;

    @Field(type = FieldType.Keyword)
    private String catalog2Name;

    @Field(type = FieldType.Long)
    private Long catalog3Id;

    @Field(type = FieldType.Keyword)
    private String catalog3Name;

    @Field(type = FieldType.Nested)
    private List<SkuSearchAttrValue> baseAttrs;

    @Field(type = FieldType.Nested)
    private List<SkuVariant> skus;

    public SpuSearchDocument() {
    }

    public SpuSearchDocument(Long spuId, String spuName, String spuMainImage, Double minPrice, Double maxPrice,
            Integer saleCount, Long brandId, String brandName, Long catalog1Id, String catalog1Name,
            Long catalog2Id, String catalog2Name, Long catalog3Id, String catalog3Name,
            List<SkuSearchAttrValue> baseAttrs, List<SkuVariant> skus) {
        this.spuId = spuId;
        this.spuName = spuName;
        this.spuMainImage = spuMainImage;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.saleCount = saleCount;
        this.brandId = brandId;
        this.brandName = brandName;
        this.catalog1Id = catalog1Id;
        this.catalog1Name = catalog1Name;
        this.catalog2Id = catalog2Id;
        this.catalog2Name = catalog2Name;
        this.catalog3Id = catalog3Id;
        this.catalog3Name = catalog3Name;
        this.baseAttrs = baseAttrs;
        this.skus = skus;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
    }

    public String getSpuMainImage() {
        return spuMainImage;
    }

    public void setSpuMainImage(String spuMainImage) {
        this.spuMainImage = spuMainImage;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Integer saleCount) {
        this.saleCount = saleCount;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }

    public Long getCatalog1Id() {
        return catalog1Id;
    }

    public void setCatalog1Id(Long catalog1Id) {
        this.catalog1Id = catalog1Id;
    }

    public String getCatalog1Name() {
        return catalog1Name;
    }

    public void setCatalog1Name(String catalog1Name) {
        this.catalog1Name = catalog1Name;
    }

    public Long getCatalog2Id() {
        return catalog2Id;
    }

    public void setCatalog2Id(Long catalog2Id) {
        this.catalog2Id = catalog2Id;
    }

    public String getCatalog2Name() {
        return catalog2Name;
    }

    public void setCatalog2Name(String catalog2Name) {
        this.catalog2Name = catalog2Name;
    }

    public Long getCatalog3Id() {
        return catalog3Id;
    }

    public void setCatalog3Id(Long catalog3Id) {
        this.catalog3Id = catalog3Id;
    }

    public String getCatalog3Name() {
        return catalog3Name;
    }

    public void setCatalog3Name(String catalog3Name) {
        this.catalog3Name = catalog3Name;
    }

    public List<SkuSearchAttrValue> getBaseAttrs() {
        return baseAttrs;
    }

    public void setBaseAttrs(List<SkuSearchAttrValue> baseAttrs) {
        this.baseAttrs = baseAttrs;
    }

    public List<SkuVariant> getSkus() {
        return skus;
    }

    public void setSkus(List<SkuVariant> skus) {
        this.skus = skus;
    }
}
