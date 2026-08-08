package com.tengan.mall.cart.application.cart;

import org.springframework.stereotype.Service;

@Service
public class ListCartService implements ListCartUseCase {

    private final CartLineAssembler assembler;

    public ListCartService(CartLineAssembler assembler) {
        this.assembler = assembler;
    }

    @Override
    public CartListResult list(CartOwner owner) {
        var items = assembler.assemble(owner);
        return new CartListResult(items, assembler.checkedTotalPrice(items), items.size());
    }
}
