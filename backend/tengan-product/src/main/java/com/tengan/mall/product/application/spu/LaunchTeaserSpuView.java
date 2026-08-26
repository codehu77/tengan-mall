package com.tengan.mall.product.application.spu;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/** price 取任一顆 SKU 的價格即可——同 SPU 底下 SKU 本來就假設同價（見 tengan-search 的 field collapse 慣例）。 */
public record LaunchTeaserSpuView(Long id, String name, String mainImage, BigDecimal price,
        LocalDateTime saleStartTime) {
}
