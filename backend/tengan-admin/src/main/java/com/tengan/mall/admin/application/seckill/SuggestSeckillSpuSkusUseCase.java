package com.tengan.mall.admin.application.seckill;

import com.tengan.mall.admin.application.port.SeckillSpuSkuSuggestion;
import java.util.List;

public interface SuggestSeckillSpuSkusUseCase {

    List<SeckillSpuSkuSuggestion> suggest(Long spuId, int totalQuota);
}
