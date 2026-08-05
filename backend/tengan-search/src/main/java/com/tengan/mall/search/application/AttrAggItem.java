package com.tengan.mall.search.application;

import java.util.List;

/** attrKey（"BASE-10"/"SALE-10"）是查詢/篩選唯一識別碼，見 SkuSearchAttrValue 的說明——BaseAttr/SaleAttr id 會撞號。 */
public record AttrAggItem(String attrKey, String attrName, List<AttrValueCount> values) {
}
