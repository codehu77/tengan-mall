package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.tengan.mall.product.domain.model.Category;
import com.tengan.mall.product.domain.repository.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryMapper categoryMapper;

    public CategoryRepositoryImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public Category save(Category category) {
        CategoryPO po = toPO(category);
        if (po.getId() == null) {
            categoryMapper.insert(po);
            category.assignId(po.getId());
        } else {
            categoryMapper.updateById(po);
        }
        return category;
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(categoryMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryMapper.selectList(null).stream().map(this::toDomain).toList();
    }

    @Override
    public boolean existsById(Long id) {
        return categoryMapper.selectById(id) != null;
    }

    @Override
    public boolean hasChildren(Long parentId) {
        LambdaQueryWrapper<CategoryPO> wrapper = new LambdaQueryWrapper<CategoryPO>().eq(CategoryPO::getParentId,
                parentId);
        return categoryMapper.selectCount(wrapper) > 0;
    }

    @Override
    public void deleteById(Long id) {
        categoryMapper.deleteById(id);
    }

    private CategoryPO toPO(Category category) {
        CategoryPO po = new CategoryPO();
        po.setId(category.getId());
        po.setParentId(category.getParentId());
        po.setName(category.getName());
        po.setIcon(category.getIcon());
        po.setSort(category.getSort());
        po.setLevel(category.getLevel());
        po.setStatus(category.getStatus());
        return po;
    }

    private Category toDomain(CategoryPO po) {
        return Category.reconstitute(po.getId(), po.getParentId(), po.getName(), po.getIcon(), po.getSort(),
                po.getLevel(), po.getStatus());
    }
}
