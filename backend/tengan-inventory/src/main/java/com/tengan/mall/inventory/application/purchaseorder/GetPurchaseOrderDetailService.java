package com.tengan.mall.inventory.application.purchaseorder;

import com.tengan.mall.inventory.domain.exception.PurchaseOrderNotFoundException;
import com.tengan.mall.inventory.domain.model.PurchaseOrder;
import com.tengan.mall.inventory.domain.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;

@Service
public class GetPurchaseOrderDetailService implements GetPurchaseOrderDetailUseCase {

    private final PurchaseOrderRepository purchaseOrderRepository;

    public GetPurchaseOrderDetailService(PurchaseOrderRepository purchaseOrderRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
    }

    @Override
    public PurchaseOrderDetailResult get(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new PurchaseOrderNotFoundException(id));
        var items = po.getItems().stream()
                .map(i -> new PurchaseOrderItemResult(i.id(), i.skuId(), i.orderedQty(), i.receivedQty()))
                .toList();
        return new PurchaseOrderDetailResult(po.getId(), po.getPoNumber(), po.getWareId(), po.getSupplierName(),
                po.getStatus().getValue(), po.getCreatedBy(), po.getCreatedAt(), po.getReceivedAt(), items);
    }
}
