package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.domain.model.BaseAttrStandardValue;
import com.tengan.mall.product.domain.repository.BaseAttrStandardValueRepository;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BaseAttrStandardValueRepositoryImpl implements BaseAttrStandardValueRepository {

    private final BaseAttrStandardValueMapper mapper;

    public BaseAttrStandardValueRepositoryImpl(BaseAttrStandardValueMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public BaseAttrStandardValue save(BaseAttrStandardValue value) {
        BaseAttrStandardValuePO po = toPO(value);
        if (po.getId() == null) {
            mapper.insert(po);
            value.assignId(po.getId());
        } else {
            mapper.updateById(po);
        }
        return value;
    }

    @Override
    public Optional<BaseAttrStandardValue> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<BaseAttrStandardValue> findByAttrId(Long attrId) {
        LambdaQueryWrapper<BaseAttrStandardValuePO> wrapper = new LambdaQueryWrapper<BaseAttrStandardValuePO>()
                .eq(BaseAttrStandardValuePO::getAttrId, attrId);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<BaseAttrStandardValue> findByAttrIds(Collection<Long> attrIds) {
        if (attrIds == null || attrIds.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<BaseAttrStandardValuePO> wrapper = new LambdaQueryWrapper<BaseAttrStandardValuePO>()
                .in(BaseAttrStandardValuePO::getAttrId, attrIds);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<BaseAttrStandardValue> findByIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        LambdaQueryWrapper<BaseAttrStandardValuePO> wrapper = new LambdaQueryWrapper<BaseAttrStandardValuePO>()
                .in(BaseAttrStandardValuePO::getId, ids);
        return mapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    private BaseAttrStandardValuePO toPO(BaseAttrStandardValue value) {
        BaseAttrStandardValuePO po = new BaseAttrStandardValuePO();
        po.setId(value.getId());
        po.setAttrId(value.getAttrId());
        po.setLabel(value.getLabel());
        po.setEnabled(value.isEnabled());
        po.setSort(value.getSort());
        return po;
    }

    private BaseAttrStandardValue toDomain(BaseAttrStandardValuePO po) {
        return BaseAttrStandardValue.reconstitute(po.getId(), po.getAttrId(), po.getLabel(),
                Boolean.TRUE.equals(po.getEnabled()), po.getSort());
    }
}
