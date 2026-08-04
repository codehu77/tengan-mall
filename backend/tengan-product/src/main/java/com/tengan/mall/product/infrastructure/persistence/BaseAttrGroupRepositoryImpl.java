package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.domain.model.BaseAttrGroup;
import com.tengan.mall.product.domain.repository.BaseAttrGroupRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BaseAttrGroupRepositoryImpl implements BaseAttrGroupRepository {

    private final BaseAttrGroupMapper baseAttrGroupMapper;

    public BaseAttrGroupRepositoryImpl(BaseAttrGroupMapper baseAttrGroupMapper) {
        this.baseAttrGroupMapper = baseAttrGroupMapper;
    }

    @Override
    public BaseAttrGroup save(BaseAttrGroup attrGroup) {
        BaseAttrGroupPO po = toPO(attrGroup);
        if (po.getId() == null) {
            baseAttrGroupMapper.insert(po);
            attrGroup.assignId(po.getId());
        } else {
            baseAttrGroupMapper.updateById(po);
        }
        return attrGroup;
    }

    @Override
    public Optional<BaseAttrGroup> findById(Long id) {
        return Optional.ofNullable(baseAttrGroupMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<BaseAttrGroup> findByCategoryId(Long categoryId) {
        LambdaQueryWrapper<BaseAttrGroupPO> wrapper = new LambdaQueryWrapper<BaseAttrGroupPO>()
                .eq(BaseAttrGroupPO::getCategoryId, categoryId);
        return baseAttrGroupMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Long> findDistinctCategoryIds() {
        return baseAttrGroupMapper.selectList(null).stream().map(BaseAttrGroupPO::getCategoryId).distinct().toList();
    }

    @Override
    public boolean existsById(Long id) {
        return baseAttrGroupMapper.selectById(id) != null;
    }

    @Override
    public void deleteById(Long id) {
        baseAttrGroupMapper.deleteById(id);
    }

    private BaseAttrGroupPO toPO(BaseAttrGroup attrGroup) {
        BaseAttrGroupPO po = new BaseAttrGroupPO();
        po.setId(attrGroup.getId());
        po.setCategoryId(attrGroup.getCategoryId());
        po.setName(attrGroup.getName());
        po.setSort(attrGroup.getSort());
        return po;
    }

    private BaseAttrGroup toDomain(BaseAttrGroupPO po) {
        return BaseAttrGroup.reconstitute(po.getId(), po.getCategoryId(), po.getName(), po.getSort());
    }
}
