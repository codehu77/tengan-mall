package com.tengan.mall.admin.application.port;

import java.math.BigDecimal;
import java.util.List;

/** 編輯既有活動的商品時，重新回查目前綁的 SPU+各規格目前的配額（不是重新按比例算的建議值），
 * 讓後台再次打開「設定商品」對話框能看到上次存的結果，不用每次都從空白開始（見 GetSeckillActivitySpuSkusService 說明）。 */
public record SeckillActivitySpuSkusResult(Long spuId, String spuName, String spuMainImage, BigDecimal seckillPrice,
        int limitPerUser, List<SeckillSpuSkuSuggestion> items) {
}
