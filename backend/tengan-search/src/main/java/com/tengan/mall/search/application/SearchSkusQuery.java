package com.tengan.mall.search.application;

import java.util.List;
import java.util.Map;

/** attrs 用 attrKey（"BASE-10"/"SALE-10"）當鍵，不是裸的 attrId——見 SkuSearchAttrValue 的說明。 */
public record SearchSkusQuery(String keyword, Long catId, List<Long> brandIds, Map<String, List<String>> attrs,
        String sort, String order, int page, int pageSize) {
}
