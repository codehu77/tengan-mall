package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.domain.model.BaseAttr;
import com.tengan.mall.product.domain.repository.BaseAttrRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BaseAttrRepositoryImpl implements BaseAttrRepository {

    private final BaseAttrMapper baseAttrMapper;

    public BaseAttrRepositoryImpl(BaseAttrMapper baseAttrMapper) {
        this.baseAttrMapper = baseAttrMapper;
    }

    @Override
    public BaseAttr save(BaseAttr attr) {
        BaseAttrPO po = toPO(attr);
        if (po.getId() == null) {
            baseAttrMapper.insert(po);
            attr.assignId(po.getId());
        } else {
            baseAttrMapper.updateById(po);
        }
        return attr;
    }

    @Override
    public Optional<BaseAttr> findById(Long id) {
        return Optional.ofNullable(baseAttrMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<BaseAttr> findByCategoryId(Long categoryId) {
        LambdaQueryWrapper<BaseAttrPO> wrapper = new LambdaQueryWrapper<BaseAttrPO>()
                .eq(BaseAttrPO::getCategoryId, categoryId);
        return baseAttrMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return baseAttrMapper.selectById(id) != null;
    }

    @Override
    public void deleteById(Long id) {
        baseAttrMapper.deleteById(id);
    }

    private BaseAttrPO toPO(BaseAttr attr) {
        BaseAttrPO po = new BaseAttrPO();
        po.setId(attr.getId());
        po.setCategoryId(attr.getCategoryId());
        po.setAttrGroupId(attr.getAttrGroupId());
        po.setName(attr.getName());
        po.setSearchable(attr.isSearchable());
        po.setSort(attr.getSort());
        return po;
    }

    private BaseAttr toDomain(BaseAttrPO po) {
        return BaseAttr.reconstitute(po.getId(), po.getCategoryId(), po.getAttrGroupId(), po.getName(),
                Boolean.TRUE.equals(po.getSearchable()), po.getSort());
    }
}
