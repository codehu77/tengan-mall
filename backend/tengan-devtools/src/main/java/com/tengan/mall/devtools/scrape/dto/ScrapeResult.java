package com.tengan.mall.devtools.scrape.dto;

import java.util.List;

/** MOMO 商品頁抓取的原始結果，純預覽用——使用者在畫面上編輯確認後才會送進 ImportController。 */
public record ScrapeResult(String sourceUrl, String name, String brandHint, List<String> spuImages,
        List<SpecHint> specHints, List<ScrapedSku> skus, List<String> featureImages, String featureText) {
}
