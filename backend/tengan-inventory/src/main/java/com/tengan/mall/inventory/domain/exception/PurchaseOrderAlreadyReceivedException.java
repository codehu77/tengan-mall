package com.tengan.mall.inventory.domain.exception;

/**
 * 這張單已經被收過貨——不是冪等 no-op，因為兩次收貨可能填不同數量，重複執行是真的衝突。
 */
public class PurchaseOrderAlreadyReceivedException extends RuntimeException {

    public PurchaseOrderAlreadyReceivedException(Long id) {
        super("此採購單已經收過貨，不可重複收貨: id=" + id);
    }
}
