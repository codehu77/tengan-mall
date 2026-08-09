package com.tengan.mall.inventory.application.purchaseorder;

import com.tengan.mall.inventory.domain.exception.PurchaseOrderAlreadyReceivedException;
import com.tengan.mall.inventory.domain.exception.PurchaseOrderNotFoundException;
import com.tengan.mall.inventory.domain.model.InventoryOperLog;
import com.tengan.mall.inventory.domain.model.PurchaseOrder;
import com.tengan.mall.inventory.domain.model.PurchaseOrderItem;
import com.tengan.mall.inventory.domain.repository.InventoryOperLogRepository;
import com.tengan.mall.inventory.domain.repository.PurchaseOrderRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 收貨：先條件式 UPDATE 搶操作權（markReceived 失敗代表已經被收過，直接拋 409，不是冪等 no-op——
 * 兩次收貨可能填不同數量，重複執行是真衝突不是重試，見 PurchaseOrderRepository 的 javadoc）。
 * 成功後對每個 item 依 existsForWareSku 決定新增或調整庫存——這是繼「手動新增庫存」之後，
 * 第二個會創造全新庫存列的合法入口。
 */
@Service
public class ReceivePurchaseOrderService implements ReceivePurchaseOrderUseCase {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final WareSkuRepository wareSkuRepository;
    private final InventoryOperLogRepository inventoryOperLogRepository;

    public ReceivePurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
            WareSkuRepository wareSkuRepository, InventoryOperLogRepository inventoryOperLogRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.wareSkuRepository = wareSkuRepository;
        this.inventoryOperLogRepository = inventoryOperLogRepository;
    }

    @Override
    @Transactional
    public void receive(ReceivePurchaseOrderCommand command) {
        PurchaseOrder po = purchaseOrderRepository.findById(command.poId())
                .orElseThrow(() -> new PurchaseOrderNotFoundException(command.poId()));

        Map<Long, Integer> itemIdToReceivedQty = command.items().stream()
                .collect(Collectors.toMap(ReceivePurchaseOrderItem::itemId, ReceivePurchaseOrderItem::receivedQty));

        if (!purchaseOrderRepository.markReceived(command.poId(), itemIdToReceivedQty)) {
            throw new PurchaseOrderAlreadyReceivedException(command.poId());
        }

        Map<Long, Long> itemIdToSkuId = po.getItems().stream()
                .collect(Collectors.toMap(PurchaseOrderItem::id, PurchaseOrderItem::skuId));

        for (var entry : itemIdToReceivedQty.entrySet()) {
            Long skuId = itemIdToSkuId.get(entry.getKey());
            int receivedQty = entry.getValue();
            if (wareSkuRepository.existsForWareSku(po.getWareId(), skuId)) {
                wareSkuRepository.adjustStockDelta(po.getWareId(), skuId, receivedQty);
            } else {
                wareSkuRepository.tryCreateInitialStock(po.getWareId(), skuId, receivedQty);
            }
        }

        inventoryOperLogRepository.save(InventoryOperLog.create(command.operator(), "purchase-order", "receive",
                "採購單收貨 poNumber=" + po.getPoNumber() + " id=" + po.getId()));
    }
}
