package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.BaseAttrCategoryMismatchException;
import com.tengan.mall.product.domain.exception.BaseAttrNotFoundException;
import com.tengan.mall.product.domain.exception.SaleAttrCategoryMismatchException;
import com.tengan.mall.product.domain.exception.SaleAttrNotFoundException;
import com.tengan.mall.product.domain.model.Sku;
import com.tengan.mall.product.domain.model.SkuImage;
import com.tengan.mall.product.domain.model.SkuSaleAttrValue;
import com.tengan.mall.product.domain.model.SpuBaseAttrValue;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import java.util.List;
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

    SpuCompositionAssembler(BaseAttrRepository baseAttrRepository, SaleAttrRepository saleAttrRepository) {
        this.baseAttrRepository = baseAttrRepository;
        this.saleAttrRepository = saleAttrRepository;
    }

    List<SpuBaseAttrValue> resolveSpuBaseAttrValues(Long categoryId, List<SpuBaseAttrValueCommand> commands) {
        return commands.stream().map(c -> {
            var attr = baseAttrRepository.findById(c.attrId())
                    .orElseThrow(() -> new BaseAttrNotFoundException(c.attrId()));
            if (!attr.getCategoryId().equals(categoryId)) {
                throw new BaseAttrCategoryMismatchException(c.attrId(), categoryId);
            }
            return new SpuBaseAttrValue(attr.getId(), attr.getName(), c.attrValue());
        }).toList();
    }

    List<Sku> buildSkus(Long categoryId, List<SkuCommand> commands) {
        return commands.stream().map(c -> buildSku(categoryId, c)).toList();
    }

    private Sku buildSku(Long categoryId, SkuCommand command) {
        List<SkuImage> images = command.images().stream()
                .map(i -> new SkuImage(i.imageUrl(), i.sort()))
                .toList();
        List<SkuSaleAttrValue> saleAttrValues = command.saleAttrValues().stream().map(c -> {
            var attr = saleAttrRepository.findById(c.attrId())
                    .orElseThrow(() -> new SaleAttrNotFoundException(c.attrId()));
            if (!attr.getCategoryId().equals(categoryId)) {
                throw new SaleAttrCategoryMismatchException(c.attrId(), categoryId);
            }
            return new SkuSaleAttrValue(attr.getId(), attr.getName(), c.attrValue());
        }).toList();
        return Sku.create(command.name(), command.price(), command.mainImage(), command.sort(), images,
                saleAttrValues);
    }
}
