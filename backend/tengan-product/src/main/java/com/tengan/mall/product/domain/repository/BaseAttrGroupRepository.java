package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.BaseAttrGroup;
import java.util.List;
import java.util.Optional;

public interface BaseAttrGroupRepository {

    BaseAttrGroup save(BaseAttrGroup attrGroup);

    Optional<BaseAttrGroup> findById(Long id);

    List<BaseAttrGroup> findByCategoryId(Long categoryId);

    List<Long> findDistinctCategoryIds();

    boolean existsById(Long id);

    void deleteById(Long id);
}
