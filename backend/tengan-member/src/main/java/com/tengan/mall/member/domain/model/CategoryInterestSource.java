package com.tengan.mall.member.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * DB 存數字、Java 用 enum 包語意（docs/資料庫設計規範.md「enum/狀態欄位」）。
 * 「猜你喜歡」分類興趣訊號的來源：VIEW=直接瀏覽商品詳情頁（明確訊號），
 * SEARCH=搜尋結果推回的分類（間接推論——這次搜尋結果裡最多商品剛好落在這個分類，
 * 不代表使用者一定對這個分類本身感興趣，只是對搜尋的東西感興趣），兩者算分權重不同，
 * 見 RecordCategoryInterestService/GetCategoryAffinityService 的權重設計。
 */
public enum CategoryInterestSource implements IEnum<Integer> {

    VIEW(1),
    SEARCH(2);

    @EnumValue
    private final int code;

    CategoryInterestSource(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
