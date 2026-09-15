package com.tengan.mall.search.application;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class SkuSearchAttrValue {

    /**
     * BaseAttr/SaleAttr 是兩張各自 auto-increment 的獨立表，attrId 會撞號（例如某分類的
     * BaseAttr「螢幕尺寸」跟 SaleAttr「顏色」剛好都是 id=10）——篩選/聚合絕對不能只靠 attrId
     * 分組，改用 {@code attrType + "-" + attrId} 這個組合鍵當作查詢跟聚合唯一識別碼，
     * attrId 保留純粹是給人看/除錯用，不再是查詢依據。
     */
    @Field(type = FieldType.Keyword)
    private String attrKey;

    @Field(type = FieldType.Long)
    private Long attrId;

    @Field(type = FieldType.Keyword)
    private String attrName;

    /** 只給關鍵字全文搜尋用（比對原始行銷值，例如「鼠尾草綠色」）——精確篩選/聚合改用 facetValue。 */
    @Field(type = FieldType.Text)
    private String attrValue;

    /**
     * 篩選/聚合真正拿來當 bucket key 的欄位：有綁定標準聚合值時是它的 label（同義字合併成一個桶），
     * 沒綁定時退回「原始值+單位」自己獨立成一個選項。這個值在 tengan-product 端就算好了，這裡原樣落地。
     */
    @Field(type = FieldType.Keyword)
    private String facetValue;

    /** 標準聚合值的拖曳排序，未綁定固定用 Integer.MAX_VALUE 排最後。 */
    @Field(type = FieldType.Integer)
    private Integer facetSort;

    public SkuSearchAttrValue() {
    }

    public SkuSearchAttrValue(String attrKey, Long attrId, String attrName, String attrValue, String facetValue,
            Integer facetSort) {
        this.attrKey = attrKey;
        this.attrId = attrId;
        this.attrName = attrName;
        this.attrValue = attrValue;
        this.facetValue = facetValue;
        this.facetSort = facetSort;
    }

    public String getAttrKey() {
        return attrKey;
    }

    public void setAttrKey(String attrKey) {
        this.attrKey = attrKey;
    }

    public Long getAttrId() {
        return attrId;
    }

    public void setAttrId(Long attrId) {
        this.attrId = attrId;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }

    public String getFacetValue() {
        return facetValue;
    }

    public void setFacetValue(String facetValue) {
        this.facetValue = facetValue;
    }

    public Integer getFacetSort() {
        return facetSort;
    }

    public void setFacetSort(Integer facetSort) {
        this.facetSort = facetSort;
    }
}
