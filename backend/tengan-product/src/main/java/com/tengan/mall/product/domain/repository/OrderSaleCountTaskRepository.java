package com.tengan.mall.product.domain.repository;

/** order.completed 消費冪等去重——UNIQUE KEY(order_sn) 保證多實例/重複投遞下只有一次真正搶到操作權。 */
public interface OrderSaleCountTaskRepository {

    /** true=第一次處理這個 orderSn（搶到操作權，繼續往下跑）；false=重複投遞，no-op。 */
    boolean tryClaim(String orderSn);
}
