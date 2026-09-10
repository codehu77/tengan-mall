package com.tengan.mall.devtools.service;

import com.tengan.mall.devtools.client.MediaApiClient;
import com.tengan.mall.devtools.client.ProductApiClient;
import com.tengan.mall.devtools.client.dto.CreateSpuRequest;
import com.tengan.mall.devtools.client.dto.SkuDraft;
import com.tengan.mall.devtools.client.dto.SkuImage;
import com.tengan.mall.devtools.client.dto.SkuSaleAttrValue;
import com.tengan.mall.devtools.client.dto.SpuBaseAttrValue;
import com.tengan.mall.devtools.client.dto.SpuImage;
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

    private final ImageDownloader imageDownloader;
    private final MediaApiClient mediaApiClient;
    private final ProductApiClient productApiClient;

    public ImportService(ImageDownloader imageDownloader, MediaApiClient mediaApiClient,
            ProductApiClient productApiClient) {
        this.imageDownloader = imageDownloader;
        this.mediaApiClient = mediaApiClient;
        this.productApiClient = productApiClient;
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

        return productApiClient.createSpu(createSpuRequest).id();
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
                : input.saleAttrValues().stream().map(v -> new SkuSaleAttrValue(v.attrId(), v.value())).toList();
        return new SkuDraft(input.name(), input.price(), uploadedImage, 0, images, saleAttrValues,
                input.purchaseLimitPerUser());
    }

    private List<SpuBaseAttrValue> mapAttrValues(List<AttrValueInput> inputs) {
        if (inputs == null) {
            return List.of();
        }
        return inputs.stream().map(v -> new SpuBaseAttrValue(v.attrId(), v.value())).toList();
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
            html.append("<p>").append(HtmlUtils.htmlEscape(featureText)).append("</p>");
        }
        for (String url : featureImageUrls) {
            html.append("<img src=\"").append(HtmlUtils.htmlEscape(url)).append("\" style=\"max-width:100%\">");
        }
        return html.isEmpty() ? null : html.toString();
    }
}
