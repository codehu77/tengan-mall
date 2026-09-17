package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.Spu;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** 收集一顆 Spu（含底下所有 Sku）目前引用的全部圖片網址，供 Delete/Update 兩邊清理 tengan-media 圖片共用。 */
final class SpuImageUrls {

    /** description 是 wangEditor/devtools 匯入產生的 HTML，裡面用 {@code <img src="...">} 內嵌圖片
     * （見 devtools {@code ImportService.buildDescriptionHtml}），這些網址不在 spu_image/sku_image
     * 結構化欄位裡，之前 collect() 沒掃到，導致這些圖片永遠停在 PENDING 被 GC 誤刪成孤兒
     * （見 spu-image-orphan-cleanup memory 記錄的真實 bug：13 個 SPU 122/225 張介紹圖遺失）。 */
    private static final Pattern IMG_SRC_PATTERN = Pattern.compile("<img[^>]*\\bsrc\\s*=\\s*[\"']([^\"']+)[\"']",
            Pattern.CASE_INSENSITIVE);

    private SpuImageUrls() {
    }

    static List<String> collect(Spu spu) {
        List<String> urls = new ArrayList<>();
        urls.add(spu.getMainImage());
        spu.getImages().forEach(image -> urls.add(image.imageUrl()));
        spu.getSkus().forEach(sku -> {
            urls.add(sku.getMainImage());
            sku.getImages().forEach(image -> urls.add(image.imageUrl()));
        });
        urls.addAll(extractDescriptionImageUrls(spu.getDescription()));
        return urls;
    }

    private static List<String> extractDescriptionImageUrls(String description) {
        if (description == null || description.isBlank()) {
            return List.of();
        }
        List<String> urls = new ArrayList<>();
        Matcher matcher = IMG_SRC_PATTERN.matcher(description);
        while (matcher.find()) {
            urls.add(matcher.group(1));
        }
        return urls;
    }
}
