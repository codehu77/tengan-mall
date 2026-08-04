package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.domain.model.Sku;
import com.tengan.mall.product.domain.model.SkuImage;
import com.tengan.mall.product.domain.model.SkuSaleAttrValue;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuBaseAttrValue;
import com.tengan.mall.product.domain.model.SpuImage;
import com.tengan.mall.product.domain.repository.SpuRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Spu 是目前這個服務裡唯一一個「聚合根 + 內部子集合」的 Repository 實作——Sku 要跟既有資料列做
 * 差異寫入（新增 insert、既有 update、被移除的 delete）；Sku 底下的 image/saleAttrValue 以及
 * Spu 底下的 attrValue 這兩層更淺的子集合沒有獨立生命週期意義，用 delete-and-reinsert 簡化。
 * 讀取一律先查父表拿 id 清單，再用 WHERE ... IN (...) 批次查子表組裝，不用巢狀迴圈逐筆查
 * （docs/資料庫設計規範.md「避免 JOIN」的批次查詢原則）。
 */
@Repository
public class SpuRepositoryImpl implements SpuRepository {

    private final SpuMapper spuMapper;
    private final SkuMapper skuMapper;
    private final SkuImageMapper skuImageMapper;
    private final SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    private final SpuBaseAttrValueMapper spuAttrValueMapper;
    private final SpuImageMapper spuImageMapper;

    public SpuRepositoryImpl(SpuMapper spuMapper, SkuMapper skuMapper, SkuImageMapper skuImageMapper,
            SkuSaleAttrValueMapper skuSaleAttrValueMapper, SpuBaseAttrValueMapper spuAttrValueMapper,
            SpuImageMapper spuImageMapper) {
        this.spuMapper = spuMapper;
        this.skuMapper = skuMapper;
        this.skuImageMapper = skuImageMapper;
        this.skuSaleAttrValueMapper = skuSaleAttrValueMapper;
        this.spuAttrValueMapper = spuAttrValueMapper;
        this.spuImageMapper = spuImageMapper;
    }

    @Override
    @Transactional
    public Spu save(Spu spu) {
        SpuPO spuPO = toSpuPO(spu);
        if (spuPO.getId() == null) {
            spuMapper.insert(spuPO);
            spu.assignId(spuPO.getId());
        } else {
            spuMapper.updateById(spuPO);
        }
        saveSkus(spu);
        saveAttrValues(spu);
        saveImages(spu);
        return spu;
    }

    private void saveSkus(Spu spu) {
        Long spuId = spu.getId();
        List<SkuPO> existing = skuMapper.selectList(new LambdaQueryWrapper<SkuPO>().eq(SkuPO::getSpuId, spuId));
        Set<Long> existingIds = existing.stream().map(SkuPO::getId).collect(Collectors.toSet());
        Set<Long> keptIds = new HashSet<>();

        for (Sku sku : spu.getSkus()) {
            SkuPO po = toSkuPO(sku, spuId);
            if (po.getId() == null) {
                skuMapper.insert(po);
                sku.assignId(po.getId());
            } else {
                skuMapper.updateById(po);
            }
            keptIds.add(po.getId());
            replaceSkuImages(po.getId(), sku.getImages());
            replaceSkuSaleAttrValues(po.getId(), sku.getSaleAttrValues());
        }

        for (Long removedId : existingIds) {
            if (!keptIds.contains(removedId)) {
                skuMapper.deleteById(removedId);
                skuImageMapper.delete(new LambdaQueryWrapper<SkuImagePO>().eq(SkuImagePO::getSkuId, removedId));
                skuSaleAttrValueMapper
                        .delete(new LambdaQueryWrapper<SkuSaleAttrValuePO>().eq(SkuSaleAttrValuePO::getSkuId, removedId));
            }
        }
    }

    private void replaceSkuImages(Long skuId, List<SkuImage> images) {
        skuImageMapper.delete(new LambdaQueryWrapper<SkuImagePO>().eq(SkuImagePO::getSkuId, skuId));
        for (SkuImage image : images) {
            SkuImagePO po = new SkuImagePO();
            po.setSkuId(skuId);
            po.setImageUrl(image.imageUrl());
            po.setSort(image.sort());
            skuImageMapper.insert(po);
        }
    }

    private void replaceSkuSaleAttrValues(Long skuId, List<SkuSaleAttrValue> saleAttrValues) {
        skuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SkuSaleAttrValuePO>().eq(SkuSaleAttrValuePO::getSkuId, skuId));
        for (SkuSaleAttrValue value : saleAttrValues) {
            SkuSaleAttrValuePO po = new SkuSaleAttrValuePO();
            po.setSkuId(skuId);
            po.setAttrId(value.attrId());
            po.setAttrName(value.attrName());
            po.setAttrValue(value.attrValue());
            skuSaleAttrValueMapper.insert(po);
        }
    }

    private void saveAttrValues(Spu spu) {
        Long spuId = spu.getId();
        spuAttrValueMapper.delete(new LambdaQueryWrapper<SpuBaseAttrValuePO>().eq(SpuBaseAttrValuePO::getSpuId, spuId));
        for (SpuBaseAttrValue value : spu.getAttrValues()) {
            SpuBaseAttrValuePO po = new SpuBaseAttrValuePO();
            po.setSpuId(spuId);
            po.setAttrId(value.attrId());
            po.setAttrName(value.attrName());
            po.setAttrValue(value.attrValue());
            spuAttrValueMapper.insert(po);
        }
    }

    @Override
    public Optional<Spu> findById(Long id) {
        SpuPO spuPO = spuMapper.selectById(id);
        if (spuPO == null) {
            return Optional.empty();
        }
        Map<Long, List<Sku>> skusBySpuId = loadSkusBySpuIds(List.of(id));
        Map<Long, List<SpuBaseAttrValue>> attrValuesBySpuId = loadAttrValuesBySpuIds(List.of(id));
        Map<Long, List<SpuImage>> imagesBySpuId = loadImagesBySpuIds(List.of(id));
        return Optional.of(toDomain(spuPO, skusBySpuId.getOrDefault(id, List.of()),
                attrValuesBySpuId.getOrDefault(id, List.of()), imagesBySpuId.getOrDefault(id, List.of())));
    }

    @Override
    public List<Spu> findAll() {
        List<SpuPO> spuPOs = spuMapper.selectList(null);
        List<Long> spuIds = spuPOs.stream().map(SpuPO::getId).toList();
        Map<Long, List<Sku>> skusBySpuId = loadSkusBySpuIds(spuIds);
        Map<Long, List<SpuBaseAttrValue>> attrValuesBySpuId = loadAttrValuesBySpuIds(spuIds);
        Map<Long, List<SpuImage>> imagesBySpuId = loadImagesBySpuIds(spuIds);
        return spuPOs.stream()
                .map(po -> toDomain(po, skusBySpuId.getOrDefault(po.getId(), List.of()),
                        attrValuesBySpuId.getOrDefault(po.getId(), List.of()),
                        imagesBySpuId.getOrDefault(po.getId(), List.of())))
                .toList();
    }

    @Override
    public boolean existsById(Long id) {
        return spuMapper.selectById(id) != null;
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        List<SkuPO> skus = skuMapper.selectList(new LambdaQueryWrapper<SkuPO>().eq(SkuPO::getSpuId, id));
        List<Long> skuIds = skus.stream().map(SkuPO::getId).toList();
        if (!skuIds.isEmpty()) {
            skuImageMapper.delete(new LambdaQueryWrapper<SkuImagePO>().in(SkuImagePO::getSkuId, skuIds));
            skuSaleAttrValueMapper.delete(new LambdaQueryWrapper<SkuSaleAttrValuePO>().in(SkuSaleAttrValuePO::getSkuId, skuIds));
        }
        skuMapper.delete(new LambdaQueryWrapper<SkuPO>().eq(SkuPO::getSpuId, id));
        spuAttrValueMapper.delete(new LambdaQueryWrapper<SpuBaseAttrValuePO>().eq(SpuBaseAttrValuePO::getSpuId, id));
        spuImageMapper.delete(new LambdaQueryWrapper<SpuImagePO>().eq(SpuImagePO::getSpuId, id));
        spuMapper.deleteById(id);
    }

    private void saveImages(Spu spu) {
        Long spuId = spu.getId();
        spuImageMapper.delete(new LambdaQueryWrapper<SpuImagePO>().eq(SpuImagePO::getSpuId, spuId));
        for (SpuImage image : spu.getImages()) {
            SpuImagePO po = new SpuImagePO();
            po.setSpuId(spuId);
            po.setImageUrl(image.imageUrl());
            po.setSort(image.sort());
            spuImageMapper.insert(po);
        }
    }

    private Map<Long, List<SpuImage>> loadImagesBySpuIds(List<Long> spuIds) {
        if (spuIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<SpuImage>> result = new java.util.HashMap<>();
        for (SpuImagePO po : spuImageMapper
                .selectList(new LambdaQueryWrapper<SpuImagePO>().in(SpuImagePO::getSpuId, spuIds))) {
            result.computeIfAbsent(po.getSpuId(), k -> new ArrayList<>())
                    .add(new SpuImage(po.getImageUrl(), po.getSort()));
        }
        return result;
    }

    private Map<Long, List<Sku>> loadSkusBySpuIds(List<Long> spuIds) {
        if (spuIds.isEmpty()) {
            return Map.of();
        }
        List<SkuPO> skuPOs = skuMapper.selectList(new LambdaQueryWrapper<SkuPO>().in(SkuPO::getSpuId, spuIds));
        List<Long> skuIds = skuPOs.stream().map(SkuPO::getId).toList();

        Map<Long, List<SkuImagePO>> imagesBySkuId = skuIds.isEmpty() ? Map.of()
                : skuImageMapper.selectList(new LambdaQueryWrapper<SkuImagePO>().in(SkuImagePO::getSkuId, skuIds))
                        .stream().collect(Collectors.groupingBy(SkuImagePO::getSkuId));
        Map<Long, List<SkuSaleAttrValuePO>> saleAttrValuesBySkuId = skuIds.isEmpty() ? Map.of()
                : skuSaleAttrValueMapper
                        .selectList(new LambdaQueryWrapper<SkuSaleAttrValuePO>().in(SkuSaleAttrValuePO::getSkuId, skuIds))
                        .stream().collect(Collectors.groupingBy(SkuSaleAttrValuePO::getSkuId));

        Map<Long, List<Sku>> result = new java.util.HashMap<>();
        for (SkuPO po : skuPOs) {
            Sku sku = toSkuDomain(po, imagesBySkuId.getOrDefault(po.getId(), List.of()),
                    saleAttrValuesBySkuId.getOrDefault(po.getId(), List.of()));
            result.computeIfAbsent(po.getSpuId(), k -> new ArrayList<>()).add(sku);
        }
        return result;
    }

    private Map<Long, List<SpuBaseAttrValue>> loadAttrValuesBySpuIds(List<Long> spuIds) {
        if (spuIds.isEmpty()) {
            return Map.of();
        }
        Map<Long, List<SpuBaseAttrValue>> result = new java.util.HashMap<>();
        for (SpuBaseAttrValuePO po : spuAttrValueMapper
                .selectList(new LambdaQueryWrapper<SpuBaseAttrValuePO>().in(SpuBaseAttrValuePO::getSpuId, spuIds))) {
            result.computeIfAbsent(po.getSpuId(), k -> new ArrayList<>())
                    .add(new SpuBaseAttrValue(po.getAttrId(), po.getAttrName(), po.getAttrValue()));
        }
        return result;
    }

    private Sku toSkuDomain(SkuPO po, List<SkuImagePO> imagePOs, List<SkuSaleAttrValuePO> saleAttrValuePOs) {
        List<SkuImage> images = imagePOs.stream()
                .map(img -> new SkuImage(img.getImageUrl(), img.getSort()))
                .toList();
        List<SkuSaleAttrValue> saleAttrValues = saleAttrValuePOs.stream()
                .map(v -> new SkuSaleAttrValue(v.getAttrId(), v.getAttrName(), v.getAttrValue()))
                .toList();
        return Sku.reconstitute(po.getId(), po.getName(), po.getPrice(), po.getMainImage(), po.getSaleCount(),
                po.getSort(), images, saleAttrValues);
    }

    private SpuPO toSpuPO(Spu spu) {
        SpuPO po = new SpuPO();
        po.setId(spu.getId());
        po.setCategoryId(spu.getCategoryId());
        po.setBrandId(spu.getBrandId());
        po.setName(spu.getName());
        po.setDescription(spu.getDescription());
        po.setMainImage(spu.getMainImage());
        po.setStatus(spu.getStatus());
        return po;
    }

    private SkuPO toSkuPO(Sku sku, Long spuId) {
        SkuPO po = new SkuPO();
        po.setId(sku.getId());
        po.setSpuId(spuId);
        po.setName(sku.getName());
        po.setPrice(sku.getPrice());
        po.setMainImage(sku.getMainImage());
        po.setSaleCount(sku.getSaleCount());
        po.setSort(sku.getSort());
        return po;
    }

    private Spu toDomain(SpuPO po, List<Sku> skus, List<SpuBaseAttrValue> attrValues, List<SpuImage> images) {
        return Spu.reconstitute(po.getId(), po.getCategoryId(), po.getBrandId(), po.getName(), po.getDescription(),
                po.getMainImage(), po.getStatus(), skus, attrValues, images);
    }
}
