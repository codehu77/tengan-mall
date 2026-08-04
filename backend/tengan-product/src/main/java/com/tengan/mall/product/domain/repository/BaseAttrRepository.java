package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.BaseAttr;
import java.util.List;
import java.util.Optional;

public interface BaseAttrRepository {

    BaseAttr save(BaseAttr attr);

    Optional<BaseAttr> findById(Long id);

    List<BaseAttr> findByCategoryId(Long categoryId);

    boolean existsById(Long id);

    void deleteById(Long id);
}
