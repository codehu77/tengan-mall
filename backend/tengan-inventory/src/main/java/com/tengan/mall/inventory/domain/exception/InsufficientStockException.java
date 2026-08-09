package com.tengan.mall.inventory.domain.exception;

/** 秒殺結算扣減（seckill/deduct）庫存不足時拋出——一般下單流程的 lock 用結構化結果回傳缺貨清單，不用例外。 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long skuId) {
        super("SKU 庫存不足，扣減失敗: skuId=" + skuId);
    }
}
