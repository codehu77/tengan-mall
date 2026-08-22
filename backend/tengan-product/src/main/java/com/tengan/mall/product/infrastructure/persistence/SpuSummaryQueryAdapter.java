package com.tengan.mall.product.infrastructure.persistence;

import com.tengan.mall.product.application.spu.SpuSummaryPort;
import com.tengan.mall.product.application.spu.SpuSummaryView;
import java.util.List;
import org.springframework.stereotype.Component;

/** SpuSummaryPort 的實作：只查 spu 表本身，不碰 sku/attrValue/image（見 SpuSummaryPort 的說明）。 */
@Component
public class SpuSummaryQueryAdapter implements SpuSummaryPort {

    private final SpuMapper spuMapper;

    public SpuSummaryQueryAdapter(SpuMapper spuMapper) {
        this.spuMapper = spuMapper;
    }

    @Override
    public List<SpuSummaryView> findByIds(List<Long> spuIds) {
        if (spuIds.isEmpty()) {
            return List.of();
        }
        return spuMapper.selectBatchIds(spuIds).stream()
                .map(po -> new SpuSummaryView(po.getId(), po.getName(), po.getMainImage()))
                .toList();
    }
}
