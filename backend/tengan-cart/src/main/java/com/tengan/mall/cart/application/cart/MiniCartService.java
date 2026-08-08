package com.tengan.mall.cart.application.cart;

import org.springframework.stereotype.Service;

@Service
public class MiniCartService implements MiniCartUseCase {

    private final CartLineAssembler assembler;

    public MiniCartService(CartLineAssembler assembler) {
        this.assembler = assembler;
    }

    @Override
    public CartListResult mini(CartOwner owner, int limit) {
        var items = assembler.assemble(owner);
        var checkedTotalPrice = assembler.checkedTotalPrice(items);
        return new CartListResult(items.stream().limit(limit).toList(), checkedTotalPrice, items.size());
    }
}
