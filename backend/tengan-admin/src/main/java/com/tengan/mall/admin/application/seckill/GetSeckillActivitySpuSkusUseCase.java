package com.tengan.mall.admin.application.seckill;

import com.tengan.mall.admin.application.port.SeckillActivitySpuSkusResult;
import java.util.List;

public interface GetSeckillActivitySpuSkusUseCase {

    /** 一場活動可以綁多個商品（SPU），依已存的 skuId 各自所屬的 spuId 分組回傳；空清單代表這場活動
     * 還沒設定過商品（DRAFT 剛建立），前端走全新選 SPU 的流程即可。 */
    List<SeckillActivitySpuSkusResult> get(Long activityId);
}
