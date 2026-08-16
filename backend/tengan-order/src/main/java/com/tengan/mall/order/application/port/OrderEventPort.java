package com.tengan.mall.order.application.port;

public interface OrderEventPort {

    /** 發 order.created 業務事件（目前無消費者，留給 Phase 7/8）+ 對 order.delay 排入逾時取消訊息。 */
    void publishOrderCreatedAndScheduleTimeout(String orderSn);

    /** 發 order.paid 事件——tengan-inventory 訂閱這個事件觸發扣庫存（locked→deducted），見 Phase 7 規劃。 */
    void publishOrderPaid(String orderSn);
}
