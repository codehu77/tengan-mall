package com.tengan.mall.wallet.application.points;

import java.util.List;

public interface GetTransactionCountsUseCase {

    List<TransactionCountGroup> get(Long memberId);
}
