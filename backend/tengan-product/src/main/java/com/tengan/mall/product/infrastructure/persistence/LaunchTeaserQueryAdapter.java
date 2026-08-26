package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.product.application.spu.LaunchTeaserQueryPort;
import com.tengan.mall.product.application.spu.LaunchTeaserSpuView;
import com.tengan.mall.product.domain.model.SpuStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * LaunchTeaserQueryPort 的實作：只查 spu 表本身（status=ON_SHELF AND show_on_launch_teaser=true AND
 * (teaser_remove_at IS NULL OR teaser_remove_at > now)）+ 批次抓每個 spu 代表 SKU 的價格（取 id 最小
 * 的那顆——同 SPU 底下 SKU 本來就假設同價），不經過 SpuRepository（見 LaunchTeaserQueryPort 的說明）。
 */
@Component
public class LaunchTeaserQueryAdapter implements LaunchTeaserQueryPort {

    private final SpuMapper spuMapper;
    private final SkuMapper skuMapper;

    public LaunchTeaserQueryAdapter(SpuMapper spuMapper, SkuMapper skuMapper) {
        this.spuMapper = spuMapper;
        this.skuMapper = skuMapper;
    }

    @Override
    public List<LaunchTeaserSpuView> listForHome(int limit) {
        List<SpuPO> records = spuMapper.selectList(buildWrapper().orderByDesc(SpuPO::getId).last("LIMIT " + limit));
        return toViews(records);
    }

    @Override
    public List<LaunchTeaserSpuView> search(int pageNum, int pageSize) {
        Page<SpuPO> page = spuMapper.selectPage(new Page<>(pageNum, pageSize), buildWrapper().orderByDesc(SpuPO::getId));
        return toViews(page.getRecords());
    }

    @Override
    public long count() {
        return spuMapper.selectCount(buildWrapper());
    }

    private List<LaunchTeaserSpuView> toViews(List<SpuPO> records) {
        List<Long> spuIds = records.stream().map(SpuPO::getId).toList();
        Map<Long, BigDecimal> priceBySpuId = representativePriceBySpuIds(spuIds);
        return records.stream()
                .map(po -> new LaunchTeaserSpuView(po.getId(), po.getName(), po.getMainImage(),
                        priceBySpuId.get(po.getId()), po.getSaleStartTime()))
                .toList();
    }

    private Map<Long, BigDecimal> representativePriceBySpuIds(List<Long> spuIds) {
        if (spuIds.isEmpty()) {
            return Map.of();
        }
        return skuMapper.selectList(new LambdaQueryWrapper<SkuPO>().in(SkuPO::getSpuId, spuIds)).stream()
                .collect(Collectors.groupingBy(SkuPO::getSpuId,
                        Collectors.collectingAndThen(Collectors.minBy(Comparator.comparing(SkuPO::getId)),
                                opt -> opt.map(SkuPO::getPrice).orElse(null))));
    }

    private LambdaQueryWrapper<SpuPO> buildWrapper() {
        LocalDateTime now = LocalDateTime.now();
        return new LambdaQueryWrapper<SpuPO>()
                .eq(SpuPO::getStatus, SpuStatus.ON_SHELF)
                .eq(SpuPO::getShowOnLaunchTeaser, true)
                .and(w -> w.isNull(SpuPO::getTeaserRemoveAt).or().gt(SpuPO::getTeaserRemoveAt, now));
    }
}
