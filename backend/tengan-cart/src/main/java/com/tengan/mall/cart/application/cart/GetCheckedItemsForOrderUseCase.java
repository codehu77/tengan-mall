package com.tengan.mall.cart.application.cart;

import java.util.List;

/** 給 tengan-order 用（internal 端點）：結帳頁組資料，只回會員購物車已勾選項目，訪客本來就走不到結帳。 */
public interface GetCheckedItemsForOrderUseCase {

    List<CheckedItemView> get(Long userId);
}
