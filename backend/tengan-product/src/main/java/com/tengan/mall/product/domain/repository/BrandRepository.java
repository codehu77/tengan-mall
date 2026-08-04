package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.Brand;
import java.util.List;
import java.util.Optional;

public interface BrandRepository {

    Brand save(Brand brand);

    Optional<Brand> findById(Long id);

    List<Brand> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);
}
