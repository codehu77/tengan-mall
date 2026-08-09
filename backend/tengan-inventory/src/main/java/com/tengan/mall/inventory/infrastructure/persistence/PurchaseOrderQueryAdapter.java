package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.inventory.application.purchaseorder.PurchaseOrderQueryPort;
import com.tengan.mall.inventory.application.purchaseorder.PurchaseOrderSummary;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class PurchaseOrderQueryAdapter implements PurchaseOrderQueryPort {

    private final PurchaseOrderMapper purchaseOrderMapper;

    public PurchaseOrderQueryAdapter(PurchaseOrderMapper purchaseOrderMapper) {
        this.purchaseOrderMapper = purchaseOrderMapper;
    }

    @Override
    public List<PurchaseOrderSummary> search(Integer status, Long wareId, int pageNum, int pageSize) {
        Page<PurchaseOrderPO> page = purchaseOrderMapper.selectPage(new Page<>(pageNum, pageSize),
                buildWrapper(status, wareId).orderByDesc(PurchaseOrderPO::getCreatedAt)
                        .orderByDesc(PurchaseOrderPO::getId));
        return page.getRecords().stream()
                .map(po -> new PurchaseOrderSummary(po.getId(), po.getPoNumber(), po.getWareId(),
                        po.getSupplierName(), po.getStatus().getValue(), toInstant(po.getCreatedAt()),
                        toInstant(po.getReceivedAt())))
                .toList();
    }

    @Override
    public long countSearch(Integer status, Long wareId) {
        return purchaseOrderMapper.selectCount(buildWrapper(status, wareId));
    }

    private LambdaQueryWrapper<PurchaseOrderPO> buildWrapper(Integer status, Long wareId) {
        LambdaQueryWrapper<PurchaseOrderPO> wrapper = new LambdaQueryWrapper<>();
        if (status != null) {
            wrapper.eq(PurchaseOrderPO::getStatus, status);
        }
        if (wareId != null) {
            wrapper.eq(PurchaseOrderPO::getWareId, wareId);
        }
        return wrapper;
    }

    private Instant toInstant(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
