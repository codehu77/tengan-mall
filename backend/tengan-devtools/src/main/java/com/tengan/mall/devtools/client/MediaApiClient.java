package com.tengan.mall.devtools.client;

import com.tengan.mall.devtools.client.dto.UploadImageResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/** 呼叫 tengan-media 的 internal 圖片上傳端點。category 固定用 "product"
 * （InternalMediaController 限制只能是 banner/product/admin-avatar，這裡不會用到其他兩種）。 */
@Component
public class MediaApiClient {

    private static final String REGISTRATION_ID = "tengan-media";
    private static final String CATEGORY = "product";

    private final RestClient mediaRestClient;
    private final ServiceTokenProvider tokenProvider;

    public MediaApiClient(RestClient mediaRestClient, ServiceTokenProvider tokenProvider) {
        this.mediaRestClient = mediaRestClient;
        this.tokenProvider = tokenProvider;
    }

    public String uploadImage(byte[] content, String filename, String contentType) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("file", new ByteArrayResource(content))
                .filename(filename)
                .contentType(MediaType.parseMediaType(contentType));
        builder.part("category", CATEGORY);

        UploadImageResponse response = mediaRestClient.post()
                .uri("/internal/media/images")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + tokenProvider.getAccessToken(REGISTRATION_ID))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(builder.build())
                .retrieve()
                .body(UploadImageResponse.class);
        if (response == null) {
            throw new IllegalStateException("上傳圖片失敗：" + filename);
        }
        return response.url();
    }
}
