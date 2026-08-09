package com.tengan.mall.inventory.application.stock;

public interface CheckStockUseCase {

    CheckStockResult check(CheckStockCommand command);
}
