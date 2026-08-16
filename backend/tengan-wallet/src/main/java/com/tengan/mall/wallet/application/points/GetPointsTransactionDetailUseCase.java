package com.tengan.mall.wallet.application.points;

public interface GetPointsTransactionDetailUseCase {

    PointsTransactionView get(Long memberId, Long id);
}
