package com.tengan.mall.product.domain.repository;

import com.tengan.mall.product.domain.model.Spu;
import java.util.List;
import java.util.Optional;

public interface SpuRepository {

    Spu save(Spu spu);

    Optional<Spu> findById(Long id);

    List<Spu> findAll();

    boolean existsById(Long id);

    void deleteById(Long id);
}
