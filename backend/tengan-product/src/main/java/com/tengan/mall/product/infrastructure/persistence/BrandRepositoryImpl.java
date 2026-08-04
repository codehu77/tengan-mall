package com.tengan.mall.product.infrastructure.persistence;

import com.tengan.mall.product.domain.model.Brand;
import com.tengan.mall.product.domain.repository.BrandRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BrandRepositoryImpl implements BrandRepository {

    private final BrandMapper brandMapper;

    public BrandRepositoryImpl(BrandMapper brandMapper) {
        this.brandMapper = brandMapper;
    }

    @Override
    public Brand save(Brand brand) {
        BrandPO po = toPO(brand);
        if (po.getId() == null) {
            brandMapper.insert(po);
            brand.assignId(po.getId());
        } else {
            brandMapper.updateById(po);
        }
        return brand;
    }

    @Override
    public Optional<Brand> findById(Long id) {
        return Optional.ofNullable(brandMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<Brand> findAll() {
        return brandMapper.selectList(null).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return brandMapper.selectById(id) != null;
    }

    @Override
    public void deleteById(Long id) {
        brandMapper.deleteById(id);
    }

    private BrandPO toPO(Brand brand) {
        BrandPO po = new BrandPO();
        po.setId(brand.getId());
        po.setName(brand.getName());
        po.setLogo(brand.getLogo());
        po.setDescript(brand.getDescript());
        po.setFirstLetter(brand.getFirstLetter());
        po.setSort(brand.getSort());
        po.setStatus(brand.getStatus());
        return po;
    }

    private Brand toDomain(BrandPO po) {
        return Brand.reconstitute(po.getId(), po.getName(), po.getLogo(), po.getDescript(), po.getFirstLetter(),
                po.getSort(), po.getStatus());
    }
}
