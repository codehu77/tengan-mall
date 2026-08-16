package com.tengan.mall.wallet.domain.exception;

public class PointsTransactionNotFoundException extends RuntimeException {

    public PointsTransactionNotFoundException(Long id) {
        super("找不到點數交易紀錄: id=" + id);
    }
}
