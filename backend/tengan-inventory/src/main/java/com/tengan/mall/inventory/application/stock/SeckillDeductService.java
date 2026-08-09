package com.tengan.mall.inventory.application.stock;

import com.tengan.mall.inventory.domain.exception.InsufficientStockException;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import org.springframework.stereotype.Service;

/**
 * 獨立輕量流程，不經過 WareOrderTask——Redis 裡的秒殺配額跟這裡的真倉庫存是分開兩本帳，這支端點
 * 是活動結算時把實際賣出量同步回真倉，文件定義的 request body 只有 {skuId,count}，沒有冪等鍵
 * （見 .docs/微服務前台API待開發清單.md 第 9 節，這是文件本身就接受的簡化，秒殺是 Phase 9 才會排的功能）。
 */
@Service
public class SeckillDeductService implements SeckillDeductUseCase {

    private final WareSkuRepository wareSkuRepository;

    public SeckillDeductService(WareSkuRepository wareSkuRepository) {
        this.wareSkuRepository = wareSkuRepository;
    }

    @Override
    public void deduct(SeckillDeductCommand command) {
        for (Long wareId : wareSkuRepository.findCandidateWareIds(command.skuId())) {
            if (wareSkuRepository.deductStockOnly(wareId, command.skuId(), command.count())) {
                return;
            }
        }
        throw new InsufficientStockException(command.skuId());
    }
}
