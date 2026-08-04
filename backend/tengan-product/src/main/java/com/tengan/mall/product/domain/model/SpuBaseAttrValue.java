package com.tengan.mall.product.domain.model;

/** Spu 的規格參數實際填值（BASE 類型），對應 Attr/AttrGroup 樣板定義的「填什麼值」。 */
public record SpuBaseAttrValue(Long attrId, String attrName, String attrValue) {
}
