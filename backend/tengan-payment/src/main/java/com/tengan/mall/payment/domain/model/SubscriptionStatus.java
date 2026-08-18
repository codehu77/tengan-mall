package com.tengan.mall.payment.domain.model;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;

/**
 * PENDING/ACTIVE 只代表「ECPay 還會不會繼續扣款」，跟「會員現在有沒有 PRO 權益」是兩件事——後者由
 * {@link Subscription#getPaidUntil()} 單獨判斷，取消/失敗停用都只會把這個欄位轉成 CANCELLED，
 * 不會連動降級（見 Subscription 類別說明）。
 *
 * <p>PENDING 代表「使用者剛按下訂閱、還沒等到 ECPay 首刷授權結果」，此時完全沒有任何權益，
 * 跟「曾經生效過、後來被取消/停用」的 CANCELLED 是不同的事——首刷失敗直接轉 CANCELLED（見
 * {@link com.tengan.mall.payment.application.webhook.HandleSubscriptionReturnCallbackService}），
 * 首刷成功才轉 ACTIVE（見 {@link com.tengan.mall.payment.application.webhook.HandlePeriodCallbackService}）。</p>
 */
public enum SubscriptionStatus implements IEnum<Integer> {

    PENDING(0),
    ACTIVE(1),
    CANCELLED(2);

    @EnumValue
    private final int code;

    SubscriptionStatus(int code) {
        this.code = code;
    }

    @Override
    public Integer getValue() {
        return code;
    }
}
