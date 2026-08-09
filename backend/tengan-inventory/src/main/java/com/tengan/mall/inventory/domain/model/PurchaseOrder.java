package com.tengan.mall.inventory.domain.model;

import java.time.Instant;
import java.util.List;

/**
 * 聚合根：採購單。root+child 模式（比照 WareOrderTask）——建立時整張連明細一起寫入。
 * 狀態轉換（PENDING→RECEIVED）不透過 load-mutate-save，而是走 PurchaseOrderRepository#markReceived
 * 的條件式 UPDATE（比照 WareOrderTaskRepository#markReleased/markDeducted，避免高併發下的競態）。
 */
public class PurchaseOrder {

    private Long id;
    private final String poNumber;
    private final Long wareId;
    private final String supplierName;
    private PurchaseOrderStatus status;
    private final String createdBy;
    private final Instant createdAt;
    private Instant receivedAt;
    private final List<PurchaseOrderItem> items;

    private PurchaseOrder(Long id, String poNumber, Long wareId, String supplierName, PurchaseOrderStatus status,
            String createdBy, Instant createdAt, Instant receivedAt, List<PurchaseOrderItem> items) {
        this.id = id;
        this.poNumber = poNumber;
        this.wareId = wareId;
        this.supplierName = supplierName;
        this.status = status;
        this.createdBy = createdBy;
        this.createdAt = createdAt;
        this.receivedAt = receivedAt;
        this.items = items;
    }

    public static PurchaseOrder create(String poNumber, Long wareId, String supplierName, String createdBy,
            List<PurchaseOrderItem> items) {
        return new PurchaseOrder(null, poNumber, wareId, supplierName, PurchaseOrderStatus.PENDING, createdBy,
                Instant.now(), null, items);
    }

    public static PurchaseOrder reconstitute(Long id, String poNumber, Long wareId, String supplierName,
            PurchaseOrderStatus status, String createdBy, Instant createdAt, Instant receivedAt,
            List<PurchaseOrderItem> items) {
        return new PurchaseOrder(id, poNumber, wareId, supplierName, status, createdBy, createdAt, receivedAt,
                items);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("PurchaseOrder 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getPoNumber() {
        return poNumber;
    }

    public Long getWareId() {
        return wareId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public PurchaseOrderStatus getStatus() {
        return status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getReceivedAt() {
        return receivedAt;
    }

    public List<PurchaseOrderItem> getItems() {
        return items;
    }
}
