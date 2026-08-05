package com.tengan.mall.search.application;

import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/**
 * 一顆 SKU = 一份 ES 文件（搜尋結果卡片是 SKU 層級，不是 SPU 層級，見 tengan-mall-web 的
 * Product 介面以 skuId 為主鍵）。這份文件是 tengan-product 组好的「厚事件」直接落地，
 * tengan-search 不回頭查任何其他服務。
 *
 * <p>skuName/spuName 用 ES 內建 standard 分詞器——沒有裝 IK/smartcn 中文分詞外掛，demo 情境下
 * 逐字切已經堪用，之後真的要精準中文分詞才需要另外處理 ES image。</p>
 */
@Document(indexName = "sku_search")
public class SkuSearchDocument {

    @Id
    private Long skuId;

    @Field(type = FieldType.Long)
    private Long spuId;

    @Field(type = FieldType.Text)
    private String skuName;

    @Field(type = FieldType.Text)
    private String spuName;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword)
    private String mainImage;

    /** SPU 層級的主圖——搜尋列表卡片是 collapse 後的商品層級呈現，圖片要用這顆而不是 mainImage（某顆代表 SKU 的圖）。 */
    @Field(type = FieldType.Keyword)
    private String spuMainImage;

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
    private List<SkuSearchAttrValue> attrs;

    public SkuSearchDocument() {
    }

    public SkuSearchDocument(Long skuId, Long spuId, String skuName, String spuName, Double price, String mainImage,
            String spuMainImage, Integer saleCount, Long brandId, String brandName, Long catalog1Id,
            String catalog1Name, Long catalog2Id, String catalog2Name, Long catalog3Id, String catalog3Name,
            List<SkuSearchAttrValue> attrs) {
        this.skuId = skuId;
        this.spuId = spuId;
        this.skuName = skuName;
        this.spuName = spuName;
        this.price = price;
        this.mainImage = mainImage;
        this.spuMainImage = spuMainImage;
        this.saleCount = saleCount;
        this.brandId = brandId;
        this.brandName = brandName;
        this.catalog1Id = catalog1Id;
        this.catalog1Name = catalog1Name;
        this.catalog2Id = catalog2Id;
        this.catalog2Name = catalog2Name;
        this.catalog3Id = catalog3Id;
        this.catalog3Name = catalog3Name;
        this.attrs = attrs;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Long getSpuId() {
        return spuId;
    }

    public void setSpuId(Long spuId) {
        this.spuId = spuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }

    public String getSpuName() {
        return spuName;
    }

    public void setSpuName(String spuName) {
        this.spuName = spuName;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public String getSpuMainImage() {
        return spuMainImage;
    }

    public void setSpuMainImage(String spuMainImage) {
        this.spuMainImage = spuMainImage;
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

    public List<SkuSearchAttrValue> getAttrs() {
        return attrs;
    }

    public void setAttrs(List<SkuSearchAttrValue> attrs) {
        this.attrs = attrs;
    }
}
