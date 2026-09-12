package com.tengan.mall.search.application;

import java.util.List;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

/** 巢狀掛在 SpuSearchDocument.skus 底下的單一規格變體，saleAttrs 是 nested-in-nested。 */
public class SkuVariant {

    @Field(type = FieldType.Long)
    private Long skuId;

    @Field(type = FieldType.Text)
    private String skuName;

    @Field(type = FieldType.Double)
    private Double price;

    @Field(type = FieldType.Keyword)
    private String mainImage;

    @Field(type = FieldType.Integer)
    private Integer saleCount;

    @Field(type = FieldType.Nested)
    private List<SkuSearchAttrValue> saleAttrs;

    public SkuVariant() {
    }

    public SkuVariant(Long skuId, String skuName, Double price, String mainImage, Integer saleCount,
            List<SkuSearchAttrValue> saleAttrs) {
        this.skuId = skuId;
        this.skuName = skuName;
        this.price = price;
        this.mainImage = mainImage;
        this.saleCount = saleCount;
        this.saleAttrs = saleAttrs;
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public String getSkuName() {
        return skuName;
    }

    public void setSkuName(String skuName) {
        this.skuName = skuName;
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

    public Integer getSaleCount() {
        return saleCount;
    }

    public void setSaleCount(Integer saleCount) {
        this.saleCount = saleCount;
    }

    public List<SkuSearchAttrValue> getSaleAttrs() {
        return saleAttrs;
    }

    public void setSaleAttrs(List<SkuSearchAttrValue> saleAttrs) {
        this.saleAttrs = saleAttrs;
    }
}
