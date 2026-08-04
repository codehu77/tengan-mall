package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-product 的「哪些分類已設定屬性」查詢端點，給左側分類樹畫提醒用。 */
public interface ProductAttrSummaryPort {

    List<Long> listCategoriesWithAttrs();
}
