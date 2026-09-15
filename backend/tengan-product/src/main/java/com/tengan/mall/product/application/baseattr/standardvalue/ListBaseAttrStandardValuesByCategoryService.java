package com.tengan.mall.product.application.baseattr.standardvalue;

import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import com.tengan.mall.product.domain.repository.BaseAttrStandardValueRepository;
import java.util.Comparator;
import org.springframework.stereotype.Service;

/** 給 SPU 精靈一次撈整個分類底下所有 BaseAttr 的標準聚合值，避免逐個屬性各打一次 API。 */
@Service
public class ListBaseAttrStandardValuesByCategoryService implements ListBaseAttrStandardValuesByCategoryUseCase {

    private final BaseAttrRepository baseAttrRepository;
    private final BaseAttrStandardValueRepository standardValueRepository;

    public ListBaseAttrStandardValuesByCategoryService(BaseAttrRepository baseAttrRepository,
            BaseAttrStandardValueRepository standardValueRepository) {
        this.baseAttrRepository = baseAttrRepository;
        this.standardValueRepository = standardValueRepository;
    }

    @Override
    public ListBaseAttrStandardValuesByCategoryResult list(ListBaseAttrStandardValuesByCategoryQuery query) {
        var attrIds = baseAttrRepository.findByCategoryId(query.categoryId()).stream().map(a -> a.getId()).toList();
        var items = standardValueRepository.findByAttrIds(attrIds).stream()
                .sorted(Comparator.comparingInt(v -> v.getSort()))
                .map(v -> new BaseAttrStandardValueSummary(v.getId(), v.getAttrId(), v.getLabel(), v.isEnabled(),
                        v.getSort()))
                .toList();
        return new ListBaseAttrStandardValuesByCategoryResult(items);
    }
}
