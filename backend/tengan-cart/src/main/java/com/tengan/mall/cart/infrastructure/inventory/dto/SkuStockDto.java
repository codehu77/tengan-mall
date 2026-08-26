package com.tengan.mall.cart.infrastructure.inventory.dto;

/** tengan-inventory 的 /api/public/inventory/skus/{skuId} 回應（SkuStockResponse）欄位子集。 */
public record SkuStockDto(Long skuId, int availableStock, boolean purchasable) {
}
