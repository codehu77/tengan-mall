package com.tengan.mall.wallet.domain.exception;

public class InsufficientPointsException extends RuntimeException {

    public InsufficientPointsException(Long memberId, int requested, int available) {
        super("點數餘額不足: memberId=" + memberId + " requested=" + requested + " available=" + available);
    }
}
