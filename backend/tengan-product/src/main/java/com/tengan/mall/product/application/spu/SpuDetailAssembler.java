package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.model.Sku;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 * GetSpuDetailService（內部，任何狀態）跟 GetPublicSpuDetailService（前台，只回 ON_SHELF）共用的映射邏輯。
 *
 * <p>unit 不冗餘存在 spu_base_attr_value/sku_sale_attr_value（不像 attrName 那樣為了存活 orphan
 * 屬性而冗餘）——純粹是顯示層格式化，每次組裝當下即時從 base_attr/sale_attr 目前的定義取值，
 * 之後改單位不需要資料遷移；orphan 屬性值（attr 樣板已刪除）單純不顯示單位。</p>
 */
@Component
class SpuDetailAssembler {

    private final CategoryRepository categoryRepository;
    private final BaseAttrRepository baseAttrRepository;
    private final SaleAttrRepository saleAttrRepository;

    SpuDetailAssembler(CategoryRepository categoryRepository, BaseAttrRepository baseAttrRepository,
            SaleAttrRepository saleAttrRepository) {
        this.categoryRepository = categoryRepository;
        this.baseAttrRepository = baseAttrRepository;
        this.saleAttrRepository = saleAttrRepository;
    }

    GetSpuDetailResult toResult(Spu spu) {
        // Collectors.toMap 遇到 null value（unit 沒設定時）會直接 NPE，這裡手動組 map 保留 null。
        Map<Long, String> baseAttrUnitById = new HashMap<>();
        baseAttrRepository.findByCategoryId(spu.getCategoryId())
                .forEach(a -> baseAttrUnitById.put(a.getId(), a.getUnit()));
        Map<Long, String> saleAttrUnitById = new HashMap<>();
        saleAttrRepository.findByCategoryId(spu.getCategoryId())
                .forEach(a -> saleAttrUnitById.put(a.getId(), a.getUnit()));

        var attrValues = spu.getAttrValues().stream()
                .map(v -> new SpuBaseAttrValueView(v.attrId(), v.attrName(), v.attrValue(), v.standardValueId(),
                        baseAttrUnitById.get(v.attrId())))
                .toList();
        var images = spu.getImages().stream()
                .map(i -> new SpuImageView(i.imageUrl(), i.sort()))
                .toList();
        var skus = spu.getSkus().stream().map(sku -> toSkuView(spu.getId(), sku, saleAttrUnitById)).toList();
        Long catalog1Id = resolveTopLevelCategoryId(spu.getCategoryId());
        return new GetSpuDetailResult(spu.getId(), spu.getCategoryId(), catalog1Id, spu.getBrandId(), spu.getName(),
                spu.getDescription(), spu.getMainImage(), spu.getStatus().getValue(), spu.getSaleStartTime(),
                spu.isShowOnLaunchTeaser(), spu.getTeaserRemoveAt(), attrValues, images, skus);
    }

    /** 從 spu 被指派的葉分類往上走 parentId 鏈，直到 level=1（最上層）為止，回傳該層 id。
     * 「猜你喜歡」的分類分數/走訪順序統一用最上層分類（catalog1）當粒度——因為 tengan-search
     * 的 catId 篩選本來就會把子分類一起算進去，用最上層分類走訪天生就能涵蓋底下所有商品，
     * 不用另外處理分類階層。只找 level=1，跟 SpuSearchDocumentAssembler.resolveAncestorChain()
     * 要組完整三層鏈的情境不同，這裡不共用那支（package-private 且回傳形狀不同）。 */
    private Long resolveTopLevelCategoryId(Long leafCategoryId) {
        Category current = categoryRepository.findById(leafCategoryId).orElse(null);
        while (current != null && current.getLevel() > 1) {
            Long parentId = current.getParentId();
            current = (parentId == null || parentId == 0L) ? null : categoryRepository.findById(parentId).orElse(null);
        }
        return current == null ? null : current.getId();
    }

    private SkuDetailView toSkuView(Long spuId, Sku sku, Map<Long, String> saleAttrUnitById) {
        var images = sku.getImages().stream()
                .map(i -> new SkuImageView(i.imageUrl(), i.sort()))
                .toList();
        var saleAttrValues = sku.getSaleAttrValues().stream()
                .map(v -> new SkuSaleAttrValueView(v.attrId(), v.attrName(), v.attrValue(), v.standardValueId(),
                        saleAttrUnitById.get(v.attrId())))
                .toList();
        return new SkuDetailView(sku.getId(), spuId, sku.getName(), sku.getPrice(), sku.getMainImage(),
                sku.getSaleCount(), sku.getSort(), sku.getPurchaseLimitPerUser(), images, saleAttrValues);
    }
}
