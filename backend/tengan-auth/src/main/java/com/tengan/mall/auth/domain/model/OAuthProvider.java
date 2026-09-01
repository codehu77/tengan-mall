package com.tengan.mall.auth.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * DB 存數字、Java 用 enum 包語意（docs/資料庫設計規範.md「enum/狀態欄位」），對齊
 * account_oauth_binding.provider 欄位註解。目前只有 GOOGLE 有實作，FACEBOOK/LINE 留給之後擴充。
 */
public enum OAuthProvider implements IEnum<Integer> {

    GOOGLE(1),
    FACEBOOK(2),
    LINE(3);

    @EnumValue
    private final int code;

    OAuthProvider(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
