package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.domain.model.SaleAttrStandardValue;
import com.tengan.mall.product.domain.repository.SaleAttrStandardValueRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class SaleAttrStandardValueRepositoryImpl implements SaleAttrStandardValueRepository {

    private final SaleAttrStandardValueMapper mapper;

    public SaleAttrStandardValueRepositoryImpl(SaleAttrStandardValueMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public SaleAttrStandardValue save(SaleAttrStandardValue value) {
        SaleAttrStandardValuePO po = toPO(value);
        if (po.getId() == null) {
            mapper.insert(po);
            value.assignId(po.getId());
        } else {
            mapper.updateById(po);
        }
        return value;
    }

    @Override
    public Optional<SaleAttrStandardValue> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<SaleAttrStandardValue> findByAttrId(Long attrId) {
        LambdaQueryWrapper<SaleAttrStandardValuePO> wrapper = new LambdaQueryWrapper<SaleAttrStandardValuePO>()
                .eq(SaleAttrStandardValuePO::getAttrId, attrId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<SaleAttrStandardValue> findByAttrIds(Collection<Long> attrIds) {
        if (attrIds == null || attrIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<SaleAttrStandardValuePO> wrapper = new LambdaQueryWrapper<SaleAttrStandardValuePO>()
                .in(SaleAttrStandardValuePO::getAttrId, attrIds);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<SaleAttrStandardValue> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<SaleAttrStandardValuePO> wrapper = new LambdaQueryWrapper<SaleAttrStandardValuePO>()
                .in(SaleAttrStandardValuePO::getId, ids);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    private SaleAttrStandardValuePO toPO(SaleAttrStandardValue value) {
        SaleAttrStandardValuePO po = new SaleAttrStandardValuePO();
        po.setId(value.getId());
        po.setAttrId(value.getAttrId());
        po.setLabel(value.getLabel());
        po.setEnabled(value.isEnabled());
        po.setSort(value.getSort());
        return po;
    }

    private SaleAttrStandardValue toDomain(SaleAttrStandardValuePO po) {
        return SaleAttrStandardValue.reconstitute(po.getId(), po.getAttrId(), po.getLabel(),
                Boolean.TRUE.equals(po.getEnabled()), po.getSort());
    }
}
