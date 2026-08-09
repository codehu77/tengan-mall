package com.tengan.mall.inventory.infrastructure.persistence;

import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class WareSkuRepositoryImpl implements WareSkuRepository {

    private final WareSkuMapper wareSkuMapper;

    public WareSkuRepositoryImpl(WareSkuMapper wareSkuMapper) {
        this.wareSkuMapper = wareSkuMapper;
    }

    @Override
    public int sumAvailableStock(Long skuId) {
        return wareSkuMapper.sumAvailableStock(skuId);
    }

    @Override
    public List<Long> findCandidateWareIds(Long skuId) {
        return wareSkuMapper.findCandidateWareIds(skuId);
    }

    @Override
    public boolean tryLock(Long wareId, Long skuId, int count) {
        return wareSkuMapper.tryLock(wareId, skuId, count) > 0;
    }

    @Override
    public void release(Long wareId, Long skuId, int count) {
        wareSkuMapper.release(wareId, skuId, count);
    }

    @Override
    public void deduct(Long wareId, Long skuId, int count) {
        wareSkuMapper.deduct(wareId, skuId, count);
    }

    @Override
    public boolean deductStockOnly(Long wareId, Long skuId, int count) {
        return wareSkuMapper.deductStockOnly(wareId, skuId, count) > 0;
    }

    @Override
    public boolean existsForWareSku(Long wareId, Long skuId) {
        return wareSkuMapper.countForWareSku(wareId, skuId) > 0;
    }

    @Override
    public boolean tryCreateInitialStock(Long wareId, Long skuId, int initialStock) {
        if (existsForWareSku(wareId, skuId)) {
            return false;
        }
        try {
            return wareSkuMapper.insertInitialStock(wareId, skuId, initialStock) > 0;
        } catch (org.springframework.dao.DuplicateKeyException e) {
            // 併發下兩個管理員同時新增同一個 (wareId, skuId)，UNIQUE KEY 擋下其中一個，視為新增失敗。
            return false;
        }
    }

    @Override
    public boolean adjustStockDelta(Long wareId, Long skuId, int delta) {
        return wareSkuMapper.adjustStockDelta(wareId, skuId, delta) > 0;
    }
}
