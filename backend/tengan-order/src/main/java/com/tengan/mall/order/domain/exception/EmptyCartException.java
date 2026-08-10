package com.tengan.mall.order.domain.exception;

/** 結帳頁沒有任何已勾選的購物車項目——不是庫存問題，在呼叫 inventory/coupon 之前就該擋下。 */
public class EmptyCartException extends RuntimeException {

    public EmptyCartException(Long memberId) {
        super("購物車沒有已勾選的項目，無法下單: memberId=" + memberId);
    }
}
