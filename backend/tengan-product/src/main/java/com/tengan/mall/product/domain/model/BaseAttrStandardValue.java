package com.tengan.mall.product.domain.model;

/**
 * 聚合根：BaseAttr 底下的「標準聚合值」（例如螢幕尺吋屬性下的「6.6 吋 ~ 7 吋」），用來把 SPU 填的
 * 原始行銷值正規化成有限的幾個桶，驅動 tengan-search 的篩選聚合。停用（enabled=false）不是真刪除——
 * 已經綁定它的 SpuBaseAttrValue 仍要能正常顯示，只是停用後不再出現在新增/編輯下拉選單裡。
 */
public class BaseAttrStandardValue {

    private Long id;
    private final Long attrId;
    private String label;
    private boolean enabled;
    private int sort;

    private BaseAttrStandardValue(Long id, Long attrId, String label, boolean enabled, int sort) {
        this.id = id;
        this.attrId = attrId;
        this.label = label;
        this.enabled = enabled;
        this.sort = sort;
    }

    public static BaseAttrStandardValue create(Long attrId, String label, int sort) {
        return new BaseAttrStandardValue(null, attrId, label, true, sort);
    }

    public static BaseAttrStandardValue reconstitute(Long id, Long attrId, String label, boolean enabled, int sort) {
        return new BaseAttrStandardValue(id, attrId, label, enabled, sort);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("BaseAttrStandardValue 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void rename(String label) {
        this.label = label;
    }

    public void updateEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void updateSort(int sort) {
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public Long getAttrId() {
        return attrId;
    }

    public String getLabel() {
        return label;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getSort() {
        return sort;
    }
}
