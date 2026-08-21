package com.tengan.mall.order.application.order;

/** 供 SeckillOrderListener（order.seckill.order.queue 消費者）呼叫，真正把秒殺訂單寫進 DB。 */
public interface MaterializeSeckillOrderUseCase {

    void materialize(MaterializeSeckillOrderCommand command);
}
