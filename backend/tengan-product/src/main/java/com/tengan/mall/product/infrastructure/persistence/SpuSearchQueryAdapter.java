package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tengan.mall.product.application.spu.SpuSearchCriteria;
import com.tengan.mall.product.application.spu.SpuSearchPort;
import com.tengan.mall.product.application.spu.SpuSummary;
import com.tengan.mall.product.domain.model.SpuStatus;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * SpuSearchPort 的實作：只查 spu 表本身 + 批次算每個 spu 的 skuCount，不載入完整 Sku 明細（image/
 * saleAttrValue 更不用查），不經過 SpuRepository/Spu 聚合根（見 SpuSearchPort 的註解）。
 */
@Component
public class SpuSearchQueryAdapter implements SpuSearchPort {

    private final SpuMapper spuMapper;
    private final SkuMapper skuMapper;

    public SpuSearchQueryAdapter(SpuMapper spuMapper, SkuMapper skuMapper) {
        this.spuMapper = spuMapper;
        this.skuMapper = skuMapper;
    }

    @Override
    public List<SpuSummary> search(SpuSearchCriteria criteria, int pageNum, int pageSize) {
        Page<SpuPO> page = spuMapper.selectPage(new Page<>(pageNum, pageSize),
                buildWrapper(criteria).orderByDesc(SpuPO::getId));
        List<SpuPO> records = page.getRecords();
        List<Long> spuIds = records.stream().map(SpuPO::getId).toList();
        Map<Long, Long> skuCountBySpuId = countSkusBySpuIds(spuIds);
        return records.stream()
                .map(po -> new SpuSummary(po.getId(), po.getCategoryId(), po.getBrandId(), po.getName(),
                        po.getMainImage(), po.getStatus().getValue(),
                        skuCountBySpuId.getOrDefault(po.getId(), 0L).intValue()))
                .toList();
    }

    @Override
    public long countSearch(SpuSearchCriteria criteria) {
        return spuMapper.selectCount(buildWrapper(criteria));
    }

    private Map<Long, Long> countSkusBySpuIds(List<Long> spuIds) {
        if (spuIds.isEmpty()) {
            return Map.of();
        }
        return skuMapper.selectList(new LambdaQueryWrapper<SkuPO>().select(SkuPO::getSpuId).in(SkuPO::getSpuId, spuIds))
                .stream().collect(Collectors.groupingBy(SkuPO::getSpuId, Collectors.counting()));
    }

    private LambdaQueryWrapper<SpuPO> buildWrapper(SpuSearchCriteria criteria) {
        LambdaQueryWrapper<SpuPO> wrapper = new LambdaQueryWrapper<>();
        if (criteria.categoryId() != null) {
            wrapper.eq(SpuPO::getCategoryId, criteria.categoryId());
        }
        if (criteria.brandId() != null) {
            wrapper.eq(SpuPO::getBrandId, criteria.brandId());
        }
        if (criteria.name() != null && !criteria.name().isBlank()) {
            wrapper.like(SpuPO::getName, criteria.name());
        }
        if (criteria.status() != null) {
            // IEnum 欄位的 MyBatis-Plus type handler 期待實際 enum 實例，不是原始 Integer，
            // 傳裸值進 wrapper.eq 會在 IEnumTypeHandler 內部轉型失敗，這裡先轉換成 SpuStatus 再查。
            SpuStatus status = Arrays.stream(SpuStatus.values())
                    .filter(s -> s.getValue().equals(criteria.status()))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Unknown spu status: " + criteria.status()));
            wrapper.eq(SpuPO::getStatus, status);
        }
        return wrapper;
    }
}
