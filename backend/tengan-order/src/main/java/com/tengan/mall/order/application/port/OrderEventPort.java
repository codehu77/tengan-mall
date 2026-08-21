package com.tengan.mall.order.application.port;

import com.tengan.mall.order.domain.model.Order;

public interface OrderEventPort {

    /** 發 order.created 業務事件（目前無消費者，留給 Phase 7/8）+ 對 order.delay 排入逾時取消訊息。 */
    void publishOrderCreatedAndScheduleTimeout(String orderSn);

    /** 發 order.paid 事件——tengan-inventory 訂閱這個事件觸發扣庫存（locked→deducted），見 Phase 7 規劃。 */
    void publishOrderPaid(String orderSn);

    /**
     * Phase 9：含秒殺項目的訂單保留成功後，不在請求執行緒內同步寫 DB，改發這個事件交給
     * {@code SeckillOrderListener} 非同步落地——保護資料庫不被搶購瞬間大量成功保留的寫入量沖垮
     * （見 Phase 9 規劃第 5 節）。傳入完整組好的 {@link Order}（此時還沒呼叫過
     * {@code orderRepository.save}，id 是 null），由 adapter 轉成 wire 格式送出。
     */
    void publishSeckillOrderCreated(Order order);
}
