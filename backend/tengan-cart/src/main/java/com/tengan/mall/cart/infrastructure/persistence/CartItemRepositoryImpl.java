package com.tengan.mall.cart.infrastructure.persistence;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.tengan.mall.cart.domain.model.CartItem;
import com.tengan.mall.cart.domain.repository.CartItemRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class CartItemRepositoryImpl implements CartItemRepository {

    private final CartItemMapper cartItemMapper;

    public CartItemRepositoryImpl(CartItemMapper cartItemMapper) {
        this.cartItemMapper = cartItemMapper;
    }

    @Override
    public CartItem save(CartItem item) {
        CartItemPO po = toPO(item);
        if (po.getId() == null) {
            cartItemMapper.insert(po);
            item.assignId(po.getId());
        } else {
            cartItemMapper.updateById(po);
        }
        return item;
    }

    @Override
    public Optional<CartItem> findById(Long id) {
        return Optional.ofNullable(cartItemMapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public Optional<CartItem> findByUserIdAndSkuId(Long userId, Long skuId) {
        LambdaQueryWrapper<CartItemPO> wrapper = new LambdaQueryWrapper<CartItemPO>()
                .eq(CartItemPO::getUserId, userId)
                .eq(CartItemPO::getSkuId, skuId);
        return Optional.ofNullable(cartItemMapper.selectOne(wrapper)).map(this::toDomain);
    }

    @Override
    public List<CartItem> findByUserId(Long userId) {
        LambdaQueryWrapper<CartItemPO> wrapper = new LambdaQueryWrapper<CartItemPO>()
                .eq(CartItemPO::getUserId, userId)
                .orderByDesc(CartItemPO::getCreatedAt);
        return cartItemMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public List<CartItem> findCheckedByUserId(Long userId) {
        LambdaQueryWrapper<CartItemPO> wrapper = new LambdaQueryWrapper<CartItemPO>()
                .eq(CartItemPO::getUserId, userId)
                .eq(CartItemPO::getChecked, true);
        return cartItemMapper.selectList(wrapper).stream().map(this::toDomain).toList();
    }

    @Override
    public int countLinesByUserId(Long userId) {
        LambdaQueryWrapper<CartItemPO> wrapper = new LambdaQueryWrapper<CartItemPO>()
                .eq(CartItemPO::getUserId, userId);
        return Math.toIntExact(cartItemMapper.selectCount(wrapper));
    }

    @Override
    public void setCheckedForUser(Long userId, boolean checked) {
        LambdaUpdateWrapper<CartItemPO> wrapper = new LambdaUpdateWrapper<CartItemPO>()
                .eq(CartItemPO::getUserId, userId)
                .set(CartItemPO::getChecked, checked);
        cartItemMapper.update(null, wrapper);
    }

    @Override
    public void deleteById(Long id) {
        cartItemMapper.deleteById(id);
    }

    @Override
    public void deleteCheckedByUserId(Long userId) {
        LambdaQueryWrapper<CartItemPO> wrapper = new LambdaQueryWrapper<CartItemPO>()
                .eq(CartItemPO::getUserId, userId)
                .eq(CartItemPO::getChecked, true);
        cartItemMapper.delete(wrapper);
    }

    @Override
    public void deleteByUserIdAndSkuIdIn(Long userId, List<Long> skuIds) {
        if (skuIds.isEmpty()) {
            return;
        }
        LambdaQueryWrapper<CartItemPO> wrapper = new LambdaQueryWrapper<CartItemPO>()
                .eq(CartItemPO::getUserId, userId)
                .in(CartItemPO::getSkuId, skuIds);
        cartItemMapper.delete(wrapper);
    }

    private CartItemPO toPO(CartItem item) {
        CartItemPO po = new CartItemPO();
        po.setId(item.getId());
        po.setUserId(item.getUserId());
        po.setSkuId(item.getSkuId());
        po.setCount(item.getCount());
        po.setChecked(item.isChecked());
        po.setSpecText(item.getSpecText());
        return po;
    }

    private CartItem toDomain(CartItemPO po) {
        return CartItem.reconstitute(po.getId(), po.getUserId(), po.getSkuId(), po.getCount(),
                Boolean.TRUE.equals(po.getChecked()), po.getSpecText());
    }
}
