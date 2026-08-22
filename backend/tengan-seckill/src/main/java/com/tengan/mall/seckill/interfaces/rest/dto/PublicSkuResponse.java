package com.tengan.mall.seckill.interfaces.rest.dto;

import java.math.BigDecimal;

/** remaining=0 代表這個規格搶購名額用完（後台故意設 0 或被搶完，兩者同一種狀態），前端要繼續顯示成已售完，不能濾掉。 */
public record PublicSkuResponse(Long skuId, String variantLabel, BigDecimal originalPrice, BigDecimal seckillPrice,
        int limitPerUser, int remaining) {
}
