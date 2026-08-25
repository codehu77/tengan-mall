package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.model.Sku;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import org.springframework.stereotype.Component;

/** GetSpuDetailService（內部，任何狀態）跟 GetPublicSpuDetailService（前台，只回 ON_SHELF）共用的映射邏輯。 */
@Component
class SpuDetailAssembler {

    private final CategoryRepository categoryRepository;

    SpuDetailAssembler(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    GetSpuDetailResult toResult(Spu spu) {
        var attrValues = spu.getAttrValues().stream()
                .map(v -> new SpuBaseAttrValueView(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        var images = spu.getImages().stream()
                .map(i -> new SpuImageView(i.imageUrl(), i.sort()))
                .toList();
        var skus = spu.getSkus().stream().map(sku -> toSkuView(spu.getId(), sku)).toList();
        Long catalog1Id = resolveTopLevelCategoryId(spu.getCategoryId());
        return new GetSpuDetailResult(spu.getId(), spu.getCategoryId(), catalog1Id, spu.getBrandId(), spu.getName(),
                spu.getDescription(), spu.getMainImage(), spu.getStatus().getValue(), attrValues, images, skus);
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

    private SkuDetailView toSkuView(Long spuId, Sku sku) {
        var images = sku.getImages().stream()
                .map(i -> new SkuImageView(i.imageUrl(), i.sort()))
                .toList();
        var saleAttrValues = sku.getSaleAttrValues().stream()
                .map(v -> new SkuSaleAttrValueView(v.attrId(), v.attrName(), v.attrValue()))
                .toList();
        return new SkuDetailView(sku.getId(), spuId, sku.getName(), sku.getPrice(), sku.getMainImage(),
                sku.getSaleCount(), sku.getSort(), images, saleAttrValues);
    }
}
