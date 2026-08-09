package com.tengan.mall.inventory.domain.exception;

/** 這次調整量會讓庫存變成負數，條件式 UPDATE 擋下（stock+delta&gt;=0）。 */
public class NegativeStockException extends RuntimeException {

    public NegativeStockException(Long wareId, Long skuId, int delta) {
        super("調整後庫存會變成負數，操作被拒絕: wareId=" + wareId + " skuId=" + skuId + " delta=" + delta);
    }
}
