package com.tengan.mall.wallet.application.points;

public interface ListPointsTransactionsUseCase {

    PointsTransactionPageResult list(ListPointsTransactionsQuery query);
}
