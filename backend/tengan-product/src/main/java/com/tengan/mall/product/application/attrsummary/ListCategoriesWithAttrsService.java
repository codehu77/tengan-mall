package com.tengan.mall.product.application.attrsummary;

import com.tengan.mall.product.domain.repository.BaseAttrGroupRepository;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

/**
 * 給左側分類樹畫「尚未設定屬性」提醒用：只要該分類有 BaseAttrGroup（就算群組底下還沒建 BaseAttr）
 * 或 SaleAttr 任一個，就算「已設定」。不查 BaseAttr 本身是因為它一定歸屬於某個 BaseAttrGroup，
 * 查 BaseAttrGroup 已經涵蓋。
 */
@Service
class ListCategoriesWithAttrsService implements ListCategoriesWithAttrsUseCase {

    private final BaseAttrGroupRepository baseAttrGroupRepository;
    private final SaleAttrRepository saleAttrRepository;

    ListCategoriesWithAttrsService(BaseAttrGroupRepository baseAttrGroupRepository,
            SaleAttrRepository saleAttrRepository) {
        this.baseAttrGroupRepository = baseAttrGroupRepository;
        this.saleAttrRepository = saleAttrRepository;
    }

    @Override
    public ListCategoriesWithAttrsResult list() {
        var categoryIds = Stream
                .concat(baseAttrGroupRepository.findDistinctCategoryIds().stream(),
                        saleAttrRepository.findDistinctCategoryIds().stream())
                .distinct()
                .toList();
        return new ListCategoriesWithAttrsResult(categoryIds);
    }
}
