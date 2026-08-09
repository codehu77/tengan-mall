package com.tengan.mall.inventory.infrastructure.persistence;

import com.tengan.mall.inventory.domain.model.PurchaseOrder;
import com.tengan.mall.inventory.domain.model.PurchaseOrderItem;
import com.tengan.mall.inventory.domain.repository.PurchaseOrderRepository;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class PurchaseOrderRepositoryImpl implements PurchaseOrderRepository {

    private final PurchaseOrderMapper purchaseOrderMapper;
    private final PurchaseOrderItemMapper purchaseOrderItemMapper;

    public PurchaseOrderRepositoryImpl(PurchaseOrderMapper purchaseOrderMapper,
            PurchaseOrderItemMapper purchaseOrderItemMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
        this.purchaseOrderItemMapper = purchaseOrderItemMapper;
    }

    @Override
    public Optional<PurchaseOrder> findById(Long id) {
        PurchaseOrderPO po = purchaseOrderMapper.selectById(id);
        if (po == null) {
            return Optional.empty();
        }
        var items = purchaseOrderItemMapper.findByPoId(po.getId()).stream()
                .map(i -> new PurchaseOrderItem(i.getId(), i.getSkuId(), i.getOrderedQty(), i.getReceivedQty()))
                .toList();
        return Optional.of(PurchaseOrder.reconstitute(po.getId(), po.getPoNumber(), po.getWareId(),
                po.getSupplierName(), po.getStatus(), po.getCreatedBy(), toInstant(po.getCreatedAt()),
                toInstant(po.getReceivedAt()), items));
    }

    @Override
    public PurchaseOrder save(PurchaseOrder po) {
        PurchaseOrderPO poPO = new PurchaseOrderPO();
        poPO.setPoNumber(po.getPoNumber());
        poPO.setWareId(po.getWareId());
        poPO.setSupplierName(po.getSupplierName());
        poPO.setStatus(po.getStatus());
        poPO.setCreatedBy(po.getCreatedBy());
        purchaseOrderMapper.insert(poPO);
        po.assignId(poPO.getId());

        for (PurchaseOrderItem item : po.getItems()) {
            PurchaseOrderItemPO itemPO = new PurchaseOrderItemPO();
            itemPO.setPoId(poPO.getId());
            itemPO.setSkuId(item.skuId());
            itemPO.setOrderedQty(item.orderedQty());
            itemPO.setReceivedQty(item.receivedQty());
            purchaseOrderItemMapper.insert(itemPO);
        }
        return po;
    }

    @Override
    public boolean markReceived(Long poId, Map<Long, Integer> itemIdToReceivedQty) {
        if (purchaseOrderMapper.markReceived(poId) == 0) {
            return false;
        }
        for (var entry : itemIdToReceivedQty.entrySet()) {
            purchaseOrderItemMapper.updateReceivedQty(entry.getKey(), entry.getValue());
        }
        return true;
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
