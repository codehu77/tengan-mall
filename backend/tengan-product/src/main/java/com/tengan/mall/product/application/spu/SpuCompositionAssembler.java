package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.BaseAttrCategoryMismatchException;
import com.tengan.mall.product.domain.exception.BaseAttrNotFoundException;
import com.tengan.mall.product.domain.exception.BaseAttrStandardValueAttrMismatchException;
import com.tengan.mall.product.domain.exception.BaseAttrStandardValueNotFoundException;
import com.tengan.mall.product.domain.exception.SaleAttrCategoryMismatchException;
import com.tengan.mall.product.domain.exception.SaleAttrNotFoundException;
import com.tengan.mall.product.domain.exception.SaleAttrStandardValueAttrMismatchException;
import com.tengan.mall.product.domain.exception.SaleAttrStandardValueNotFoundException;
import com.tengan.mall.product.domain.exception.SkuIdMismatchException;
import com.tengan.mall.product.domain.model.Sku;
import com.tengan.mall.product.domain.model.SkuImage;
import com.tengan.mall.product.domain.model.SkuSaleAttrValue;
import com.tengan.mall.product.domain.model.SpuBaseAttrValue;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.BaseAttrStandardValueRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import com.tengan.mall.product.domain.repository.SaleAttrStandardValueRepository;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * CreateSpuService/UpdateSpuService 共用的組裝邏輯：把 Command 裡的 attrId 解析成完整的
 * attrName/attrValue，並驗證 attr 屬於同一個 categoryId。不是聚合根方法，因為要跨聚合根查
 * BaseAttrRepository/SaleAttrRepository；不放進 Create/Update 各自的 Service，因為兩邊邏輯完全相同。
 *
 * <p>BASE/SALE 拆成 {@code BaseAttr}/{@code SaleAttr} 兩個獨立聚合根之後，這裡不再需要驗證
 * attrType 對不對——查哪個 Repository 本身就決定了型別，結構上不可能把 BaseAttr 的 id 誤填進
 * sku_sale_attr_value（會直接查不到、丟 NotFoundException），舊版靠 attrType 分支判斷的
 * AttrTypeMismatchException 已經沒有存在的必要。</p>
 */
@Component
class SpuCompositionAssembler {

    private final BaseAttrRepository baseAttrRepository;
    private final SaleAttrRepository saleAttrRepository;
    private final BaseAttrStandardValueRepository baseAttrStandardValueRepository;
    private final SaleAttrStandardValueRepository saleAttrStandardValueRepository;

    SpuCompositionAssembler(BaseAttrRepository baseAttrRepository, SaleAttrRepository saleAttrRepository,
            BaseAttrStandardValueRepository baseAttrStandardValueRepository,
            SaleAttrStandardValueRepository saleAttrStandardValueRepository) {
        this.baseAttrRepository = baseAttrRepository;
        this.saleAttrRepository = saleAttrRepository;
        this.baseAttrStandardValueRepository = baseAttrStandardValueRepository;
        this.saleAttrStandardValueRepository = saleAttrStandardValueRepository;
    }

    List<SpuBaseAttrValue> resolveSpuBaseAttrValues(Long categoryId, List<SpuBaseAttrValueCommand> commands) {
        return commands.stream().map(c -> {
            var attr = baseAttrRepository.findById(c.attrId())
                    .orElseThrow(() -> new BaseAttrNotFoundException(c.attrId()));
            if (!attr.getCategoryId().equals(categoryId)) {
                throw new BaseAttrCategoryMismatchException(c.attrId(), categoryId);
            }
            if (c.standardValueId() != null) {
                var std = baseAttrStandardValueRepository.findById(c.standardValueId())
                        .orElseThrow(() -> new BaseAttrStandardValueNotFoundException(c.standardValueId()));
                if (!std.getAttrId().equals(attr.getId())) {
                    throw new BaseAttrStandardValueAttrMismatchException(c.standardValueId(), attr.getId());
                }
            }
            return new SpuBaseAttrValue(attr.getId(), attr.getName(), c.attrValue(), c.standardValueId());
        }).toList();
    }

    /**
     * existingSkus 是這個 Spu「這次更新前」的既有 SKU 清單（Create 情境傳空清單即可，新商品沒有既有
     * SKU）。command.id() 非 null 代表編輯既有規格：從 existingSkus 找回原本的 id/saleCount，不能
     * 只信任 client 傳來的 id——找不到就代表這個 id 不屬於這個 Spu（可能是別的 SPU 底下的 skuId
     * 誤填/竄改進來），直接拒絕，不能讓 updateById 誤改到別的商品的 SKU 列。
     */
    List<Sku> buildSkus(Long categoryId, List<SkuCommand> commands, List<Sku> existingSkus, Long spuId) {
        Map<Long, Sku> existingById = existingSkus.stream().collect(Collectors.toMap(Sku::getId, Function.identity()));
        return commands.stream().map(c -> buildSku(categoryId, c, existingById, spuId)).toList();
    }

    private Sku buildSku(Long categoryId, SkuCommand command, Map<Long, Sku> existingById, Long spuId) {
        List<SkuImage> images = command.images().stream()
                .map(i -> new SkuImage(i.imageUrl(), i.sort()))
                .toList();
        List<SkuSaleAttrValue> saleAttrValues = command.saleAttrValues().stream().map(c -> {
            var attr = saleAttrRepository.findById(c.attrId())
                    .orElseThrow(() -> new SaleAttrNotFoundException(c.attrId()));
            if (!attr.getCategoryId().equals(categoryId)) {
                throw new SaleAttrCategoryMismatchException(c.attrId(), categoryId);
            }
            if (c.standardValueId() != null) {
                var std = saleAttrStandardValueRepository.findById(c.standardValueId())
                        .orElseThrow(() -> new SaleAttrStandardValueNotFoundException(c.standardValueId()));
                if (!std.getAttrId().equals(attr.getId())) {
                    throw new SaleAttrStandardValueAttrMismatchException(c.standardValueId(), attr.getId());
                }
            }
            return new SkuSaleAttrValue(attr.getId(), attr.getName(), c.attrValue(), c.standardValueId());
        }).toList();

        if (command.id() == null) {
            return Sku.create(command.name(), command.price(), command.mainImage(), command.sort(), images,
                    saleAttrValues, command.purchaseLimitPerUser());
        }
        Sku existing = existingById.get(command.id());
        if (existing == null) {
            throw new SkuIdMismatchException(command.id(), spuId);
        }
        return Sku.reconstitute(existing.getId(), command.name(), command.price(), command.mainImage(),
                existing.getSaleCount(), command.sort(), command.purchaseLimitPerUser(), images, saleAttrValues);
    }
}
