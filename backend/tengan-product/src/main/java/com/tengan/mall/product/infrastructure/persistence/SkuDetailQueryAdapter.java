package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.application.spu.SkuDetailPort;
import com.tengan.mall.product.application.spu.SkuDetailView;
import com.tengan.mall.product.application.spu.SkuImageView;
import com.tengan.mall.product.application.spu.SkuSaleAttrValueView;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
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
    private final SaleAttrRepository saleAttrRepository;

    public SkuDetailQueryAdapter(SkuMapper skuMapper, SkuImageMapper skuImageMapper,
            SkuSaleAttrValueMapper skuSaleAttrValueMapper, SaleAttrRepository saleAttrRepository) {
        this.skuMapper = skuMapper;
        this.skuImageMapper = skuImageMapper;
        this.skuSaleAttrValueMapper = skuSaleAttrValueMapper;
        this.saleAttrRepository = saleAttrRepository;
    }

    /** 每顆 SKU 的銷售屬性數量很小（通常 1-3 個），逐一 findById 解析 unit 即可，不需要額外開 batch 查詢方法。 */
    private Map<Long, String> resolveUnits(Set<Long> attrIds) {
        Map<Long, String> unitById = new HashMap<>();
        attrIds.forEach(id -> saleAttrRepository.findById(id).ifPresent(a -> unitById.put(id, a.getUnit())));
        return unitById;
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
        List<SkuSaleAttrValuePO> saleAttrValuePOs = skuSaleAttrValueMapper
                .selectList(new LambdaQueryWrapper<SkuSaleAttrValuePO>().eq(SkuSaleAttrValuePO::getSkuId, skuId));
        Map<Long, String> unitById = resolveUnits(
                saleAttrValuePOs.stream().map(SkuSaleAttrValuePO::getAttrId).collect(Collectors.toSet()));
        var saleAttrValues = saleAttrValuePOs.stream()
                .map(v -> new SkuSaleAttrValueView(v.getAttrId(), v.getAttrName(), v.getAttrValue(),
                        v.getStandardValueId(), unitById.get(v.getAttrId())))
                .toList();

        return Optional.of(new SkuDetailView(po.getId(), po.getSpuId(), po.getName(), po.getPrice(),
                po.getMainImage(), po.getSaleCount(), po.getSort(), po.getPurchaseLimitPerUser(), images,
                saleAttrValues));
    }

    @Override
    public List<SkuDetailView> findByIds(List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return List.of();
        }
        List<SkuPO> skuPOs = skuMapper.selectBatchIds(skuIds);
        if (skuPOs.isEmpty()) {
            return List.of();
        }
        List<Long> foundIds = skuPOs.stream().map(SkuPO::getId).toList();

        Map<Long, List<SkuImageView>> imagesBySkuId = skuImageMapper
                .selectList(new LambdaQueryWrapper<SkuImagePO>().in(SkuImagePO::getSkuId, foundIds))
                .stream()
                .collect(Collectors.groupingBy(SkuImagePO::getSkuId,
                        Collectors.mapping(i -> new SkuImageView(i.getImageUrl(), i.getSort()), Collectors.toList())));
        List<SkuSaleAttrValuePO> allSaleAttrValuePOs = skuSaleAttrValueMapper
                .selectList(new LambdaQueryWrapper<SkuSaleAttrValuePO>().in(SkuSaleAttrValuePO::getSkuId, foundIds));
        Map<Long, String> unitById = resolveUnits(
                allSaleAttrValuePOs.stream().map(SkuSaleAttrValuePO::getAttrId).collect(Collectors.toSet()));
        Map<Long, List<SkuSaleAttrValueView>> saleAttrValuesBySkuId = allSaleAttrValuePOs.stream()
                .collect(Collectors.groupingBy(SkuSaleAttrValuePO::getSkuId, Collectors.mapping(
                        v -> new SkuSaleAttrValueView(v.getAttrId(), v.getAttrName(), v.getAttrValue(),
                                v.getStandardValueId(), unitById.get(v.getAttrId())),
                        Collectors.toList())));

        return skuPOs.stream()
                .map(po -> new SkuDetailView(po.getId(), po.getSpuId(), po.getName(), po.getPrice(),
                        po.getMainImage(), po.getSaleCount(), po.getSort(), po.getPurchaseLimitPerUser(),
                        imagesBySkuId.getOrDefault(po.getId(), List.of()),
                        saleAttrValuesBySkuId.getOrDefault(po.getId(), List.of())))
                .toList();
    }
}
