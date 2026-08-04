package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.SaleAttr;
import java.util.List;
import java.util.Optional;

public interface SaleAttrRepository {

    SaleAttr save(SaleAttr attr);

    Optional<SaleAttr> findById(Long id);

    List<SaleAttr> findByCategoryId(Long categoryId);

    List<Long> findDistinctCategoryIds();

    boolean existsById(Long id);

    void deleteById(Long id);
}
