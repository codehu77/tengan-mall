package com.tengan.mall.inventory.application.purchaseorder;

import com.tengan.mall.inventory.domain.model.InventoryOperLog;
import com.tengan.mall.inventory.domain.model.PurchaseOrder;
import com.tengan.mall.inventory.domain.model.PurchaseOrderItem;
import com.tengan.mall.inventory.domain.repository.InventoryOperLogRepository;
import com.tengan.mall.inventory.domain.repository.PurchaseOrderRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** 建立 PENDING 採購單——poNumber 服務層產生（PO+時間戳+3位亂數），不用 DB 序列表。 */
@Service
public class CreatePurchaseOrderService implements CreatePurchaseOrderUseCase {

    private static final DateTimeFormatter PO_NUMBER_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryOperLogRepository inventoryOperLogRepository;

    public CreatePurchaseOrderService(PurchaseOrderRepository purchaseOrderRepository,
            InventoryOperLogRepository inventoryOperLogRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.inventoryOperLogRepository = inventoryOperLogRepository;
    }

    @Override
    @Transactional
    public Long create(CreatePurchaseOrderCommand command) {
        var items = command.items().stream()
                .map(i -> new PurchaseOrderItem(null, i.skuId(), i.orderedQty(), null))
                .toList();
        String poNumber = generatePoNumber();
        PurchaseOrder po = PurchaseOrder.create(poNumber, command.wareId(), command.supplierName(),
                command.operator(), items);
        purchaseOrderRepository.save(po);

        inventoryOperLogRepository.save(InventoryOperLog.create(command.operator(), "purchase-order", "create",
                "建立採購單 poNumber=" + poNumber + " wareId=" + command.wareId()));
        return po.getId();
    }

    private String generatePoNumber() {
        String timestamp = PO_NUMBER_TIMESTAMP.format(LocalDateTime.now());
        int random = ThreadLocalRandom.current().nextInt(1000);
        return "PO" + timestamp + String.format("%03d", random);
    }
}
