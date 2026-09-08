package com.tengan.mall.devtools.scrape.dto;

import java.math.BigDecimal;

public record ScrapedSku(String name, String sourceSkuCode, String mainImage, BigDecimal price) {
}
