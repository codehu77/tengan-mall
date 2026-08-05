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

    @Field(type = FieldType.Keyword)
    private String attrValue;

    public SkuSearchAttrValue() {
    }

    public SkuSearchAttrValue(String attrKey, Long attrId, String attrName, String attrValue) {
        this.attrKey = attrKey;
        this.attrId = attrId;
        this.attrName = attrName;
        this.attrValue = attrValue;
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
}
