package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.BaseAttr;
import com.tengan.mall.product.domain.model.BaseAttrStandardValue;
import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.model.SaleAttr;
import com.tengan.mall.product.domain.model.SaleAttrStandardValue;
import com.tengan.mall.product.domain.model.Sku;
import com.tengan.mall.product.domain.model.SkuSaleAttrValue;
import com.tengan.mall.product.domain.model.Spu;
import com.tengan.mall.product.domain.model.SpuBaseAttrValue;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.BaseAttrStandardValueRepository;
import com.tengan.mall.product.domain.repository.BrandRepository;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import com.tengan.mall.product.domain.repository.SaleAttrStandardValueRepository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Component;

/**
 * 「厚事件」的組裝邏輯——把一個已載入完整 skus/attrValues 的 Spu 聚合根，組成一份 SPU 層級的
 * 搜尋文件（分類祖先鏈、品牌名稱、只含 searchable=true 的屬性值，minPrice/maxPrice 索引階段就算好，
 * 每顆 sku 的規格明細巢狀掛在 skus 裡供規格篩選）。tengan-search 收到事件後不用回頭查任何其他服務，
 * 這裡就是唯一需要做這件事的地方。
 */
@Component
class SpuSearchDocumentAssembler {

    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final BaseAttrRepository baseAttrRepository;
    private final SaleAttrRepository saleAttrRepository;
    private final BaseAttrStandardValueRepository baseAttrStandardValueRepository;
    private final SaleAttrStandardValueRepository saleAttrStandardValueRepository;

    SpuSearchDocumentAssembler(CategoryRepository categoryRepository, BrandRepository brandRepository,
            BaseAttrRepository baseAttrRepository, SaleAttrRepository saleAttrRepository,
            BaseAttrStandardValueRepository baseAttrStandardValueRepository,
            SaleAttrStandardValueRepository saleAttrStandardValueRepository) {
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.baseAttrRepository = baseAttrRepository;
        this.saleAttrRepository = saleAttrRepository;
        this.baseAttrStandardValueRepository = baseAttrStandardValueRepository;
        this.saleAttrStandardValueRepository = saleAttrStandardValueRepository;
    }

    SpuSearchDocumentPayload assemble(Spu spu) {
        Category[] ancestors = resolveAncestorChain(spu.getCategoryId());
        String brandName = brandRepository.findById(spu.getBrandId()).map(b -> b.getName()).orElse(null);

        // BaseAttr/SaleAttr 是兩張各自 auto-increment 的獨立表，id 會撞號——絕對不能合併成同一個
        // Set<Long> 再用同一份集合去篩兩種來源的值，那樣會把「BaseAttr id=10 可搜尋」誤套用到
        // 「SaleAttr id=10」上（反之亦然），篩選結果會跟屬性定義本身的 searchable 標記對不上。
        Map<Long, BaseAttr> searchableBaseAttrs = new HashMap<>();
        baseAttrRepository.findByCategoryId(spu.getCategoryId()).stream()
                .filter(BaseAttr::isSearchable)
                .forEach(a -> searchableBaseAttrs.put(a.getId(), a));
        Map<Long, SaleAttr> searchableSaleAttrs = new HashMap<>();
        saleAttrRepository.findByCategoryId(spu.getCategoryId()).stream()
                .filter(SaleAttr::isSearchable)
                .forEach(a -> searchableSaleAttrs.put(a.getId(), a));

        List<SpuBaseAttrValue> searchableBaseAttrValues = spu.getAttrValues().stream()
                .filter(v -> searchableBaseAttrs.containsKey(v.attrId())).toList();
        List<SkuSaleAttrValue> searchableSaleAttrValues = spu.getSkus().stream()
                .flatMap(sku -> sku.getSaleAttrValues().stream())
                .filter(v -> searchableSaleAttrs.containsKey(v.attrId())).toList();

        Set<Long> baseStandardValueIds = searchableBaseAttrValues.stream().map(SpuBaseAttrValue::standardValueId)
                .filter(id -> id != null).collect(java.util.stream.Collectors.toSet());
        Set<Long> saleStandardValueIds = searchableSaleAttrValues.stream().map(SkuSaleAttrValue::standardValueId)
                .filter(id -> id != null).collect(java.util.stream.Collectors.toSet());
        Map<Long, BaseAttrStandardValue> baseStandardValueById = new HashMap<>();
        baseAttrStandardValueRepository.findByIds(baseStandardValueIds)
                .forEach(v -> baseStandardValueById.put(v.getId(), v));
        Map<Long, SaleAttrStandardValue> saleStandardValueById = new HashMap<>();
        saleAttrStandardValueRepository.findByIds(saleStandardValueIds)
                .forEach(v -> saleStandardValueById.put(v.getId(), v));

        List<SearchAttrPayload> baseAttrs = searchableBaseAttrValues.stream()
                .map(v -> toBaseAttrPayload(v, searchableBaseAttrs.get(v.attrId()), baseStandardValueById))
                .toList();

        List<SkuVariantPayload> skus = new ArrayList<>();
        for (Sku sku : spu.getSkus()) {
            List<SearchAttrPayload> saleAttrs = sku.getSaleAttrValues().stream()
                    .filter(v -> searchableSaleAttrs.containsKey(v.attrId()))
                    .map(v -> toSaleAttrPayload(v, searchableSaleAttrs.get(v.attrId()), saleStandardValueById))
                    .toList();
            skus.add(new SkuVariantPayload(sku.getId(), sku.getName(), sku.getPrice(), sku.getMainImage(),
                    sku.getSaleCount(), saleAttrs));
        }

        BigDecimal minPrice = skus.stream().map(SkuVariantPayload::price).min(Comparator.naturalOrder())
                .orElse(null);
        BigDecimal maxPrice = skus.stream().map(SkuVariantPayload::price).max(Comparator.naturalOrder())
                .orElse(null);
        int totalSaleCount = skus.stream().mapToInt(SkuVariantPayload::saleCount).sum();

        return new SpuSearchDocumentPayload(spu.getId(), spu.getName(), spu.getMainImage(), minPrice, maxPrice,
                totalSaleCount, spu.getBrandId(), brandName, idOrNull(ancestors[0]), nameOrNull(ancestors[0]),
                idOrNull(ancestors[1]), nameOrNull(ancestors[1]), idOrNull(ancestors[2]), nameOrNull(ancestors[2]),
                baseAttrs, skus);
    }

    /**
     * facetValue 是搜尋聚合/篩選真正拿來當 bucket key 的欄位：有綁定標準聚合值就用它的 label（同一個
     * 桶不管幾種原始行銷寫法都合併），沒綁定就退回「原始值+單位」自己獨立成一個選項（舊資料未綁定時
     * 仍要能被篩選）。facetSort 讓 tengan-search 端能照後台拖曳的順序排列，未綁定的排最後。
     */
    private SearchAttrPayload toBaseAttrPayload(SpuBaseAttrValue v, BaseAttr attr,
            Map<Long, BaseAttrStandardValue> standardValueById) {
        BaseAttrStandardValue std = v.standardValueId() == null ? null : standardValueById.get(v.standardValueId());
        String unit = attr.getUnit();
        String facetValue = std != null ? std.getLabel() : v.attrValue() + (unit == null ? "" : unit);
        int facetSort = std != null ? std.getSort() : Integer.MAX_VALUE;
        return new SearchAttrPayload(v.attrId(), "BASE", v.attrName(), v.attrValue(), facetValue, facetSort);
    }

    private SearchAttrPayload toSaleAttrPayload(SkuSaleAttrValue v, SaleAttr attr,
            Map<Long, SaleAttrStandardValue> standardValueById) {
        SaleAttrStandardValue std = v.standardValueId() == null ? null : standardValueById.get(v.standardValueId());
        String unit = attr.getUnit();
        String facetValue = std != null ? std.getLabel() : v.attrValue() + (unit == null ? "" : unit);
        int facetSort = std != null ? std.getSort() : Integer.MAX_VALUE;
        return new SearchAttrPayload(v.attrId(), "SALE", v.attrName(), v.attrValue(), facetValue, facetSort);
    }

    /** 分類只存單一 parentId，往上走最多 3 層組出 [level1, level2, level3] 祖先鏈（含自己）。 */
    private Category[] resolveAncestorChain(Long leafCategoryId) {
        Category[] chain = new Category[3];
        Category current = categoryRepository.findById(leafCategoryId).orElse(null);
        while (current != null && current.getLevel() >= 1 && current.getLevel() <= 3) {
            chain[current.getLevel() - 1] = current;
            Long parentId = current.getParentId();
            current = (parentId == null || parentId == 0L) ? null
                    : categoryRepository.findById(parentId).orElse(null);
        }
        return chain;
    }

    private Long idOrNull(Category category) {
        return category == null ? null : category.getId();
    }

    private String nameOrNull(Category category) {
        return category == null ? null : category.getName();
    }
}
