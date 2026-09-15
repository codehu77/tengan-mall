package com.tengan.mall.product.application.saleattr.standardvalue;

import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import com.tengan.mall.product.domain.repository.SaleAttrStandardValueRepository;
import java.util.Comparator;
import org.springframework.stereotype.Service;

/** 給 SPU 精靈一次撈整個分類底下所有 SaleAttr 的標準聚合值，避免逐個屬性各打一次 API。 */
@Service
public class ListSaleAttrStandardValuesByCategoryService implements ListSaleAttrStandardValuesByCategoryUseCase {

    private final SaleAttrRepository saleAttrRepository;
    private final SaleAttrStandardValueRepository standardValueRepository;

    public ListSaleAttrStandardValuesByCategoryService(SaleAttrRepository saleAttrRepository,
            SaleAttrStandardValueRepository standardValueRepository) {
        this.saleAttrRepository = saleAttrRepository;
        this.standardValueRepository = standardValueRepository;
    }

    @Override
    public ListSaleAttrStandardValuesByCategoryResult list(ListSaleAttrStandardValuesByCategoryQuery query) {
        var attrIds = saleAttrRepository.findByCategoryId(query.categoryId()).stream().map(a -> a.getId()).toList();
        var items = standardValueRepository.findByAttrIds(attrIds).stream()
                .sorted(Comparator.comparingInt(v -> v.getSort()))
                .map(v -> new SaleAttrStandardValueSummary(v.getId(), v.getAttrId(), v.getLabel(), v.isEnabled(),
                        v.getSort()))
                .toList();
        return new ListSaleAttrStandardValuesByCategoryResult(items);
    }
}
