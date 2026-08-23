package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.MediaUploadPort;
import com.tengan.mall.admin.interfaces.rest.dto.UploadImageResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 後台素材上傳（Banner 圖/商品圖），跟顧客頭像上傳分開命名空間。category 白名單（banner/product）
 * 的驗證交給 tengan-media 自己做（UploadImageService），這裡不重複驗證——驗證失敗時 tengan-media
 * 回的 400 會被既有的 {@link AdminExceptionHandler#handleDownstreamProductError} 原樣轉發。
 */
@RestController
@RequestMapping("/api/admin/media/images")
public class MediaUploadController {

    private final MediaUploadPort mediaUploadPort;

    public MediaUploadController(MediaUploadPort mediaUploadPort) {
        this.mediaUploadPort = mediaUploadPort;
    }

    @PostMapping
    @PreAuthorize("hasAuthority('media:banner:write') or hasAuthority('product:spu:write')")
    public UploadImageResponse uploadImage(@RequestParam("file") MultipartFile file,
            @RequestParam("category") String category) {
        try {
            String url = mediaUploadPort.uploadImage(file.getBytes(), file.getOriginalFilename(),
                    file.getContentType(), category, null);
            return new UploadImageResponse(url);
        } catch (IOException e) {
            throw new UncheckedIOException("讀取上傳檔案失敗", e);
        }
    }
}
