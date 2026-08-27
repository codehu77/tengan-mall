package com.tengan.mall.inventory.application.gate;

import java.util.List;

public interface GetGateStatusUseCase {

    /** 只回傳有資料的 spuId，從沒設定過防超賣保護的 spuId 不會出現在結果裡。 */
    List<GateConfigView> getBySpuIds(List<Long> spuIds);
}
