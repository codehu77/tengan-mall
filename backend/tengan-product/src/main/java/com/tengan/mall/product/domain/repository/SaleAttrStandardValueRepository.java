package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.SaleAttrStandardValue;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface SaleAttrStandardValueRepository {

    SaleAttrStandardValue save(SaleAttrStandardValue value);

    Optional<SaleAttrStandardValue> findById(Long id);

    List<SaleAttrStandardValue> findByAttrId(Long attrId);

    List<SaleAttrStandardValue> findByAttrIds(Collection<Long> attrIds);

    List<SaleAttrStandardValue> findByIds(Collection<Long> ids);
}
