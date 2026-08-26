package com.tengan.mall.product.interfaces.rest.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LaunchTeaserSpuResponse(Long id, String name, String mainImage, BigDecimal price,
        LocalDateTime saleStartTime) {
}
