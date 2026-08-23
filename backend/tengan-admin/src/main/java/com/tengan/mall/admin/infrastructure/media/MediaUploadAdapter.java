package com.tengan.mall.admin.infrastructure.media;

import com.tengan.mall.admin.application.port.MediaUploadPort;
import com.tengan.mall.admin.infrastructure.media.dto.UploadImageEnvelope;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * 全站第一次做「服務對服務轉發 multipart」——瀏覽器上傳的檔案位元組先進到 tengan-admin
 * 這支 BFF，再包成 multipart 請求轉發給 tengan-media。{@link ByteArrayResource} 預設
 * {@code getFilename()} 回傳 null，MinIO 那邊要靠原始檔名取副檔名決定 content-type，所以這裡
 * 覆寫回傳原始檔名。file part 一定要包成帶明確 Content-Type header 的 {@link HttpEntity}——
 * 如果只丟裸的 Resource，Spring 的 multipart converter 會自己用檔名副檔名猜 content-type
 * （{@code MediaTypeFactory}），檔名沒有可辨識副檔名（例如螢幕截圖工具貼上的檔案常常沒有）
 * 就會猜成 {@code application/octet-stream}，被 tengan-media 的白名單擋掉，回一個看起來
 * 跟真正檔案格式完全無關的「不支援的檔案格式」錯誤，很難聯想到根因。
 */
@Component
public class MediaUploadAdapter implements MediaUploadPort {

    private final RestClient mediaRestClient;
    private final MediaServiceTokenProvider tokenProvider;

    public MediaUploadAdapter(RestClient mediaRestClient, MediaServiceTokenProvider tokenProvider) {
        this.mediaRestClient = mediaRestClient;
        this.tokenProvider = tokenProvider;
    }

    @Override
    public String uploadImage(byte[] content, String originalFilename, String contentType, String category,
            Long ownerId) {
        ByteArrayResource fileResource = new ByteArrayResource(content) {
            @Override
            public String getFilename() {
                return originalFilename;
            }
        };
        HttpHeaders filePartHeaders = new HttpHeaders();
        // banner/product 上傳（MediaUploadController）不在這裡先驗證 contentType，交給 tengan-media
        // 的白名單判斷——contentType 缺失或格式不對時，寧可退回 octet-stream 讓下游的白名單擋掉，
        // 也不要在這裡就讓 MediaType.parseMediaType 直接丟例外，變成看起來像轉發機制壞掉的 500。
        filePartHeaders.setContentType(
                contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM);
        HttpEntity<Resource> filePart = new HttpEntity<>(fileResource, filePartHeaders);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", filePart);
        body.add("category", category);
        if (ownerId != null) {
            body.add("ownerId", ownerId);
        }

        UploadImageEnvelope envelope = mediaRestClient.post()
                .uri("/internal/media/images")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(UploadImageEnvelope.class);
        if (envelope == null) {
            throw new IllegalStateException("上傳圖片呼叫無回應");
        }
        return envelope.url();
    }
}
