package com.tengan.mall.cart.application.cart;

import java.math.BigDecimal;
import java.util.List;

/**
 * MiniCartService 也重用這個型別——items 可能是被 limit 裁切過的子集，totalItemCount 才是完整
 * 購物車的行數（不是 checkedTotalPrice 那種金額），給前端 header mini-cart 算「還有 N 項未顯示」用。
 */
public record CartListResult(List<CartLineView> items, BigDecimal checkedTotalPrice, int totalItemCount) {
}
