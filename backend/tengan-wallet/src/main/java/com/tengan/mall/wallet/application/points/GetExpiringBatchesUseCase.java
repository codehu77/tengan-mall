package com.tengan.mall.wallet.application.points;

import java.util.List;

public interface GetExpiringBatchesUseCase {

    List<PointBatchView> get(Long memberId);
}
