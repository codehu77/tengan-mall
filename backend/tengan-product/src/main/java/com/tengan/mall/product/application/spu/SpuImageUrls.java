package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.Spu;
import java.util.ArrayList;
import java.util.List;

/** 收集一顆 Spu（含底下所有 Sku）目前引用的全部圖片網址，供 Delete/Update 兩邊清理 tengan-media 圖片共用。 */
final class SpuImageUrls {

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
        return urls;
    }
}
