package com.tengan.mall.product.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * DB 存數字、Java 用 enum 包語意（docs/資料庫設計規範.md「enum/狀態欄位」）。
 * 四態：NEW=剛建立還沒上架、ON_SHELF=上架中、OFF_SHELF=下架（曾經上架過又下架，不是刪除）、
 * DUPLICATE=複製出來的草稿，還沒被人工編輯確認過，不能直接上架（見 Spu.publish()/updateBasicInfo()）。
 */
public enum SpuStatus implements IEnum<Integer> {

    NEW(0),
    ON_SHELF(1),
    OFF_SHELF(2),
    DUPLICATE(3);

    @EnumValue
    private final int code;

    SpuStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
