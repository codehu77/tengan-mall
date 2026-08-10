package com.tengan.mall.order.domain.exception;

/** 顧客查詢/操作了不屬於自己的訂單——不洩漏「訂單存在但不是你的」，呼叫端統一當 404 處理。 */
public class OrderAccessDeniedException extends RuntimeException {

    public OrderAccessDeniedException(String orderSn) {
        super("無權存取此訂單: orderSn=" + orderSn);
    }
}
