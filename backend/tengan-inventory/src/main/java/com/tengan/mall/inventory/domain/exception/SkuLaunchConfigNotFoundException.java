package com.tengan.mall.inventory.domain.exception;

/** 這顆 skuId 從未透過 product.launch-config.upserted 事件同步過，還不知道它的開賣時間。 */
public class SkuLaunchConfigNotFoundException extends RuntimeException {

    public SkuLaunchConfigNotFoundException(Long skuId) {
        super("尚未同步此 SKU 的商品設定，無法設定庫存流量閘門: skuId=" + skuId);
    }
}
