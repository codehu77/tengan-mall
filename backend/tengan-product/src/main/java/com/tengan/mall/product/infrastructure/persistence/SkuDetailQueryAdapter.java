package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.application.spu.SkuDetailPort;
import com.tengan.mall.product.application.spu.SkuDetailView;
import com.tengan.mall.product.application.spu.SkuImageView;
import com.tengan.mall.product.application.spu.SkuSaleAttrValueView;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * SkuDetailPort 的實作：只查 sku 本身 + 它的 image/saleAttrValue，不碰 spu 表也不查其他兄弟
 * sku——這是刻意的 CQRS-lite 查詢，不經過 SpuRepository/Spu 聚合根（見 SkuDetailPort 的註解）。
 */
@Component
public class SkuDetailQueryAdapter implements SkuDetailPort {

    private final SkuMapper skuMapper;
    private final SkuImageMapper skuImageMapper;
    private final SkuSaleAttrValueMapper skuSaleAttrValueMapper;

    public SkuDetailQueryAdapter(SkuMapper skuMapper, SkuImageMapper skuImageMapper,
            SkuSaleAttrValueMapper skuSaleAttrValueMapper) {
        this.skuMapper = skuMapper;
        this.skuImageMapper = skuImageMapper;
        this.skuSaleAttrValueMapper = skuSaleAttrValueMapper;
    }

    @Override
    public Optional<SkuDetailView> findById(Long skuId) {
        SkuPO po = skuMapper.selectById(skuId);
        if (po == null) {
            return Optional.empty();
        }

        var images = skuImageMapper.selectList(new LambdaQueryWrapper<SkuImagePO>().eq(SkuImagePO::getSkuId, skuId))
                .stream()
                .map(i -> new SkuImageView(i.getImageUrl(), i.getSort()))
                .toList();
        var saleAttrValues = skuSaleAttrValueMapper
                .selectList(new LambdaQueryWrapper<SkuSaleAttrValuePO>().eq(SkuSaleAttrValuePO::getSkuId, skuId))
                .stream()
                .map(v -> new SkuSaleAttrValueView(v.getAttrId(), v.getAttrName(), v.getAttrValue()))
                .toList();

        return Optional.of(new SkuDetailView(po.getId(), po.getSpuId(), po.getName(), po.getPrice(),
                po.getMainImage(), po.getSaleCount(), po.getSort(), images, saleAttrValues));
    }
}
