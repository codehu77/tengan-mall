package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.BaseAttrStandardValue;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface BaseAttrStandardValueRepository {

    BaseAttrStandardValue save(BaseAttrStandardValue value);

    Optional<BaseAttrStandardValue> findById(Long id);

    List<BaseAttrStandardValue> findByAttrId(Long attrId);

    List<BaseAttrStandardValue> findByAttrIds(Collection<Long> attrIds);

    List<BaseAttrStandardValue> findByIds(Collection<Long> ids);
}
