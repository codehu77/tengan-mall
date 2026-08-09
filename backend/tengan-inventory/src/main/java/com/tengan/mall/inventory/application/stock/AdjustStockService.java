package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.exception.NegativeStockException;
import com.tengan.mall.inventory.domain.exception.WareSkuNotFoundException;
import com.tengan.mall.inventory.domain.model.InventoryOperLog;
import com.tengan.mall.inventory.domain.repository.InventoryOperLogRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 人工盤點修正——增減量（delta）而不是覆蓋絕對值，比照 lock/release/deduct 同樣的條件式 UPDATE
 * 精神：避免兩個管理員前後腳的操作互相覆蓋掉彼此的異動，也避免修正後變成負庫存。原因(reason)存進
 * oper_log，提供最小限度的稽核軌跡（不做完整的庫存流水帳表，見這次的設計討論）。
 */
@Service
public class AdjustStockService implements AdjustStockUseCase {

    private final WareSkuRepository wareSkuRepository;
    private final InventoryOperLogRepository inventoryOperLogRepository;

    public AdjustStockService(WareSkuRepository wareSkuRepository,
            InventoryOperLogRepository inventoryOperLogRepository) {
        this.wareSkuRepository = wareSkuRepository;
        this.inventoryOperLogRepository = inventoryOperLogRepository;
    }

    @Override
    @Transactional
    public void adjust(AdjustStockCommand command) {
        if (!wareSkuRepository.existsForWareSku(command.wareId(), command.skuId())) {
            throw new WareSkuNotFoundException(command.wareId(), command.skuId());
        }
        if (!wareSkuRepository.adjustStockDelta(command.wareId(), command.skuId(), command.delta())) {
            throw new NegativeStockException(command.wareId(), command.skuId(), command.delta());
        }

        inventoryOperLogRepository.save(InventoryOperLog.create(command.operator(), "stock", "adjust",
                "調整庫存 wareId=" + command.wareId() + " skuId=" + command.skuId() + " delta="
                        + (command.delta() >= 0 ? "+" : "") + command.delta() + " 原因=" + command.reason()));
    }
}
