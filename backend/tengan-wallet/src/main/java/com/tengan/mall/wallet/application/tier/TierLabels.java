package com.tengan.mall.wallet.application.tier;

import com.tengan.mall.wallet.domain.model.MemberTierLevel;

/** 三個等級的顯示文案，客服後台跟顧客前台共用同一份，避免兩邊各自硬編碼一套不同的名稱。 */
final class TierLabels {

    private TierLabels() {
    }

    static String labelOf(MemberTierLevel tier) {
        return switch (tier) {
            case FREE -> "一般會員";
            case PRO -> "PRO 會員";
            case PRO_PLUS -> "PRO+ 會員";
        };
    }
}
