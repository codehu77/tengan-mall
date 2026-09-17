package com.tengan.mall.devtools.service;

import com.tengan.mall.devtools.client.InventoryApiClient;
import com.tengan.mall.devtools.client.MediaApiClient;
import com.tengan.mall.devtools.client.ProductApiClient;
import com.tengan.mall.devtools.client.dto.CreateSpuRequest;
import com.tengan.mall.devtools.client.dto.SkuDraft;
import com.tengan.mall.devtools.client.dto.SkuImage;
import com.tengan.mall.devtools.client.dto.SkuSaleAttrValue;
import com.tengan.mall.devtools.client.dto.SpuBaseAttrValue;
import com.tengan.mall.devtools.client.dto.SpuImage;
import com.tengan.mall.devtools.client.dto.Warehouse;
import com.tengan.mall.devtools.service.ImageDownloader.DownloadedImage;
import com.tengan.mall.devtools.web.dto.AttrValueInput;
import com.tengan.mall.devtools.web.dto.ImportRequest;
import com.tengan.mall.devtools.web.dto.SkuInput;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

/** 把使用者在畫面上編輯確認後的資料落地：先把每張圖片下載下來重新上傳到 tengan-media 換自家 URL，
 * 再組成 tengan-product 的 CreateSpuRequest 送出去。 */
@Service
public class ImportService {

    /** 使用者要求匯入時順便幫新 SKU 建庫存，預設存進第一個倉庫（目前只有一個倉庫，尚無「哪個是預設倉」
     * 的欄位可查），數量固定 100 顆，之後要調整再回後台庫存管理頁手動改。 */
    private static final int DEFAULT_INITIAL_STOCK = 100;

    private final ImageDownloader imageDownloader;
    private final MediaApiClient mediaApiClient;
    private final ProductApiClient productApiClient;
    private final InventoryApiClient inventoryApiClient;

    public ImportService(ImageDownloader imageDownloader, MediaApiClient mediaApiClient,
            ProductApiClient productApiClient, InventoryApiClient inventoryApiClient) {
        this.imageDownloader = imageDownloader;
        this.mediaApiClient = mediaApiClient;
        this.productApiClient = productApiClient;
        this.inventoryApiClient = inventoryApiClient;
    }

    public Long importProduct(ImportRequest request) {
        List<String> spuImageUrls = reuploadAll(request.spuImageUrls());
        String mainImage = spuImageUrls.isEmpty() ? null : spuImageUrls.get(resolveMainImageIndex(request));
        List<SpuImage> spuImages = new ArrayList<>();
        for (int i = 0; i < spuImageUrls.size(); i++) {
            spuImages.add(new SpuImage(spuImageUrls.get(i), i));
        }

        List<String> featureImageUrls = reuploadAll(
                request.featureImageUrls() == null ? List.of() : request.featureImageUrls());
        String description = buildDescriptionHtml(request.featureText(), featureImageUrls);

        List<SpuBaseAttrValue> attrValues = mapAttrValues(request.baseAttrValues());
        List<SkuDraft> skus = request.skus().stream().map(this::toSkuDraft).toList();

        CreateSpuRequest createSpuRequest = new CreateSpuRequest(request.categoryId(), request.brandId(),
                request.name(), description, mainImage, null, false, null, attrValues, spuImages, skus);

        Long spuId = productApiClient.createSpu(createSpuRequest).id();
        seedInitialStock(spuId);
        return spuId;
    }

    private void seedInitialStock(Long spuId) {
        List<Warehouse> warehouses = inventoryApiClient.listWarehouses();
        if (warehouses.isEmpty()) {
            throw new IllegalStateException("tengan-inventory 目前沒有任何倉庫，無法建立初始庫存");
        }
        Long defaultWareId = warehouses.get(0).id();
        for (var sku : productApiClient.getSpu(spuId).skus()) {
            inventoryApiClient.createStock(defaultWareId, sku.id(), DEFAULT_INITIAL_STOCK);
        }
    }

    /** 使用者勾的是重新上傳「前」的來源網址，reuploadAll 保留原本順序，所以來源清單裡的位置
     * 直接對應重新上傳後清單的同一個位置。沒選、或選到的網址不在清單裡（理論上不會發生，
     * 防呆而已），就退回原本「取第一張」的行為。 */
    private int resolveMainImageIndex(ImportRequest request) {
        if (request.mainImageUrl() == null) {
            return 0;
        }
        int index = request.spuImageUrls().indexOf(request.mainImageUrl());
        return index >= 0 ? index : 0;
    }

    private SkuDraft toSkuDraft(SkuInput input) {
        String uploadedImage = input.imageUrl() == null || input.imageUrl().isBlank() ? null
                : reupload(input.imageUrl());
        List<SkuImage> images = uploadedImage == null ? List.of() : List.of(new SkuImage(uploadedImage, 0));
        List<SkuSaleAttrValue> saleAttrValues = input.saleAttrValues() == null ? List.of()
                : input.saleAttrValues().stream()
                        .map(v -> new SkuSaleAttrValue(v.attrId(), v.value(), v.standardValueId())).toList();
        return new SkuDraft(input.name(), input.price(), uploadedImage, 0, images, saleAttrValues,
                input.purchaseLimitPerUser());
    }

    private List<SpuBaseAttrValue> mapAttrValues(List<AttrValueInput> inputs) {
        if (inputs == null) {
            return List.of();
        }
        return inputs.stream().map(v -> new SpuBaseAttrValue(v.attrId(), v.value(), v.standardValueId())).toList();
    }

    private List<String> reuploadAll(List<String> sourceUrls) {
        return sourceUrls.stream().map(this::reupload).toList();
    }

    private String reupload(String sourceUrl) {
        DownloadedImage image = imageDownloader.download(sourceUrl);
        return mediaApiClient.uploadImage(image.content(), image.filename(), image.contentType());
    }

    private String buildDescriptionHtml(String featureText, List<String> featureImageUrls) {
        StringBuilder html = new StringBuilder();
        if (featureText != null && !featureText.isBlank()) {
            // MomoScraperService 擷取特色文字時，已經把賣家原本的段落結構（<p>/<div>/<br>）轉成 \n
            // 換行字元保留下來——這裡照原本的段落切開，每段各自包一個 <p>，不要合併成一大塊文字。
            for (String paragraph : featureText.split("\n")) {
                String trimmed = paragraph.strip();
                if (!trimmed.isEmpty()) {
                    html.append("<p>").append(HtmlUtils.htmlEscape(trimmed)).append("</p>");
                }
            }
        }
        for (String url : featureImageUrls) {
            // wangEditor（tengan-admin-web 後台編輯 description 用的富文字編輯器）只保證認得
            // editor.getHtml() 自己吐出來的格式，不是通用 HTML 解析器。除了 <img> 要有 alt/data-href
            // 屬性，結構上也不能讓多張圖片直接裸連成一串——wizard.vue 的 CSS 註解記過 wangEditor
            // 自己插入「連續多張圖、中間不夾文字」時，每張圖片是各自獨立包一個 <p>。裸連的 <img>
            // 兄弟節點會在編輯器正規化文件結構時被當異常清掉（不需要真的去動圖片，任何一次編輯
            // 動作觸發的重新整理就會發生），之前商品介紹圖片就是這樣消失的。
            html.append("<p><img src=\"").append(HtmlUtils.htmlEscape(url))
                    .append("\" alt=\"\" data-href=\"\" style=\"\"/></p>");
        }
        return html.isEmpty() ? null : html.toString();
    }
}
