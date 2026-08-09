package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.inventory.application.stock.WareSkuQueryItem;
import com.tengan.mall.inventory.application.stock.WareSkuQueryPort;
import org.springframework.stereotype.Component;

@Component
public class WareSkuQueryAdapter implements WareSkuQueryPort {

    private final WareSkuMapper wareSkuMapper;

    public WareSkuQueryAdapter(WareSkuMapper wareSkuMapper) {
        this.wareSkuMapper = wareSkuMapper;
    }

    @Override
    public java.util.List<WareSkuQueryItem> search(Long wareId, Long skuIdKeyword, int pageNum, int pageSize) {
        Page<WareSkuPO> page = wareSkuMapper.selectPage(new Page<>(pageNum, pageSize),
                buildWrapper(wareId, skuIdKeyword).orderByAsc(WareSkuPO::getWareId).orderByAsc(WareSkuPO::getSkuId));
        return page.getRecords().stream()
                .map(po -> new WareSkuQueryItem(po.getWareId(), po.getSkuId(), po.getStock(), po.getLockedStock()))
                .toList();
    }

    @Override
    public long countSearch(Long wareId, Long skuIdKeyword) {
        return wareSkuMapper.selectCount(buildWrapper(wareId, skuIdKeyword));
    }

    private LambdaQueryWrapper<WareSkuPO> buildWrapper(Long wareId, Long skuIdKeyword) {
        LambdaQueryWrapper<WareSkuPO> wrapper = new LambdaQueryWrapper<>();
        if (wareId != null) {
            wrapper.eq(WareSkuPO::getWareId, wareId);
        }
        if (skuIdKeyword != null) {
            wrapper.likeRight(WareSkuPO::getSkuId, skuIdKeyword);
        }
        return wrapper;
    }
}
