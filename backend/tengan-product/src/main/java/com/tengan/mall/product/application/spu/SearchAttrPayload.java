package com.tengan.mall.product.application.spu;

/**
 * 搜尋文件裡的單一屬性值——只包含 searchable=true 的 BaseAttr/SaleAttr。
 *
 * <p>{@code attrType}（"BASE"/"SALE"）是這裡才需要的傳輸層標記，不是 domain 概念——{@code BaseAttr}/
 * {@code SaleAttr} 是兩張各自 auto-increment 的獨立表，id 會撞號（例如某分類的 BaseAttr「螢幕尺寸」
 * 跟 SaleAttr「顏色」剛好都是 id=10）。tengan-search 那邊拿 attrId 分組篩選/聚合時，沒有這個標記會把
 * 兩個不相干的屬性誤判成同一組。純字串常數，不是把已經拆掉的 domain {@code AttrType} 概念復活。</p>
 */
public record SearchAttrPayload(Long attrId, String attrType, String attrName, String attrValue) {
}
