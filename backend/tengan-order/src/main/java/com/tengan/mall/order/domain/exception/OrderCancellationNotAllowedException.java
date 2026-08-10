package com.tengan.mall.order.domain.exception;

/**
 * 只有 PENDING_PAYMENT 才能取消——已付款訂單不能取消，這跟「退款功能刻意不做」的定案一致
 * （admin_api_gap_fixes），不處理退款分支。
 */
public class OrderCancellationNotAllowedException extends RuntimeException {

    public OrderCancellationNotAllowedException(String orderSn) {
        super("此訂單目前狀態不允許取消: orderSn=" + orderSn);
    }
}
