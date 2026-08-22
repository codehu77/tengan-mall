package com.tengan.mall.seckill.application.display;

import java.math.BigDecimal;

/** remaining=0 代表這個規格搶購名額用完（不管是後台故意設 0 還是被搶完），前端要繼續顯示、只是禁止選購，不能濾掉。 */
public record ActiveSkuView(Long skuId, String variantLabel, BigDecimal originalPrice, BigDecimal seckillPrice,
        int limitPerUser, int remaining) {
}
