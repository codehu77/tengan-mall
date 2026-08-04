package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.domain.model.SaleAttr;
import com.tengan.mall.product.domain.repository.SaleAttrRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SaleAttrRepositoryImpl implements SaleAttrRepository {

    private final SaleAttrMapper saleAttrMapper;

    public SaleAttrRepositoryImpl(SaleAttrMapper saleAttrMapper) {
        this.saleAttrMapper = saleAttrMapper;
    }

    @Override
    public SaleAttr save(SaleAttr attr) {
        SaleAttrPO po = toPO(attr);
        if (po.getId() == null) {
            saleAttrMapper.insert(po);
            attr.assignId(po.getId());
        } else {
            saleAttrMapper.updateById(po);
        }
        return attr;
    }

    @Override
    public Optional<SaleAttr> findById(Long id) {
        return Optional.ofNullable(saleAttrMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<SaleAttr> findByCategoryId(Long categoryId) {
        LambdaQueryWrapper<SaleAttrPO> wrapper = new LambdaQueryWrapper<SaleAttrPO>()
                .eq(SaleAttrPO::getCategoryId, categoryId);
        return saleAttrMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Long> findDistinctCategoryIds() {
        return saleAttrMapper.selectList(null).stream().map(SaleAttrPO::getCategoryId).distinct().toList();
    }

    @Override
    public boolean existsById(Long id) {
        return saleAttrMapper.selectById(id) != null;
    }

    @Override
    public void deleteById(Long id) {
        saleAttrMapper.deleteById(id);
    }

    private SaleAttrPO toPO(SaleAttr attr) {
        SaleAttrPO po = new SaleAttrPO();
        po.setId(attr.getId());
        po.setCategoryId(attr.getCategoryId());
        po.setName(attr.getName());
        po.setSearchable(attr.isSearchable());
        po.setSort(attr.getSort());
        return po;
    }

    private SaleAttr toDomain(SaleAttrPO po) {
        return SaleAttr.reconstitute(po.getId(), po.getCategoryId(), po.getName(),
                Boolean.TRUE.equals(po.getSearchable()), po.getSort());
    }
}
