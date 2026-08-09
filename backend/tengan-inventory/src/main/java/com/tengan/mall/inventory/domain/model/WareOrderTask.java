package com.tengan.mall.inventory.domain.model;

import java.util.List;

/**
 * 聚合根：庫存工作單。root+child 模式（比照 Spu/Sku）——lock 成功時整張連明細一起寫入，
 * 保證「有工作單就代表庫存真的鎖成功」這個不變條件不會有中間不一致狀態。
 */
public class WareOrderTask {

    private Long id;
    private final String orderSn;
    private WareTaskStatus status;
    private final List<WareOrderTaskDetail> details;

    private WareOrderTask(Long id, String orderSn, WareTaskStatus status, List<WareOrderTaskDetail> details) {
        this.id = id;
        this.orderSn = orderSn;
        this.status = status;
        this.details = details;
    }

    public static WareOrderTask lock(String orderSn, List<WareOrderTaskDetail> details) {
        return new WareOrderTask(null, orderSn, WareTaskStatus.LOCKED, details);
    }

    public static WareOrderTask reconstitute(Long id, String orderSn, WareTaskStatus status,
            List<WareOrderTaskDetail> details) {
        return new WareOrderTask(id, orderSn, status, details);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("WareOrderTask 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public WareTaskStatus getStatus() {
        return status;
    }

    public List<WareOrderTaskDetail> getDetails() {
        return details;
    }
}
