package com.tengan.mall.cart.application.cart;

import java.math.BigDecimal;

/**
 * 購物車項目的攤平投影，即時查價後的結果。itemId 依身份而異——會員是 cart_item 的 db id，
 * 訪客沒有 db id，直接沿用 skuId 當識別（訪客購物車本來就是每個 skuId 一行，見
 * cart_storage_decision）；後續操作（改數量/勾選/刪除）都用這個 itemId 當路徑參數，語意一致，
 * 呼叫端不需要知道底層儲存差異。price/name/mainImage/spuId 在 sku 已被刪除（不是下架，下架仍查
 * 得到）時會是 null，available=false 讓前端知道這個項目已經不能結帳——spuId 是給前端商品連結用
 * （詳情頁走 spu 導向路由，不是 sku，見 2026-08-06 的 MOMO 討論支線）。
 */
public record CartLineView(Long itemId, Long skuId, Long spuId, String name, BigDecimal price, String mainImage,
        int count, boolean checked, String specText, boolean available) {
}
