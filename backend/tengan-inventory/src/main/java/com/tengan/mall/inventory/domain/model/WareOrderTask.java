package com.tengan.mall.inventory.domain.model;

import java.util.List;

/**
 * 聚合根：庫存工作單。root+child 模式（比照 Spu/Sku）——lock 成功時整張連明細一起寫入，
 * 保證「有工作單就代表庫存真的鎖成功」這個不變條件不會有中間不一致狀態。
 */
public class WareOrderTask {

    private Long id;
    private final String orderSn;
    private final Long memberId;
    private WareTaskStatus status;
    private final List<WareOrderTaskDetail> details;

    private WareOrderTask(Long id, String orderSn, Long memberId, WareTaskStatus status,
            List<WareOrderTaskDetail> details) {
        this.id = id;
        this.orderSn = orderSn;
        this.memberId = memberId;
        this.status = status;
        this.details = details;
    }

    /** memberId 是 Redis 閘門保留(見 WareOrderTaskDetail.wareId()==null 的明細)release 時要用來重建限購計數 key，可能為 null(歷史資料/純真倉鎖定的呼叫)。 */
    public static WareOrderTask lock(String orderSn, Long memberId, List<WareOrderTaskDetail> details) {
        return new WareOrderTask(null, orderSn, memberId, WareTaskStatus.LOCKED, details);
    }

    public static WareOrderTask reconstitute(Long id, String orderSn, Long memberId, WareTaskStatus status,
            List<WareOrderTaskDetail> details) {
        return new WareOrderTask(id, orderSn, memberId, status, details);
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

    public Long getMemberId() {
        return memberId;
    }

    public WareTaskStatus getStatus() {
        return status;
    }

    public List<WareOrderTaskDetail> getDetails() {
        return details;
    }
}
