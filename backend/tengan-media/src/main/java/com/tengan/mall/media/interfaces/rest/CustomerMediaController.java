package com.tengan.mall.media.interfaces.rest;

import com.tengan.mall.media.application.upload.UploadImageCommand;
import com.tengan.mall.media.application.upload.UploadImageUseCase;
import com.tengan.mall.media.interfaces.rest.dto.UploadImageResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 顧客端目前唯一用得到的上傳情境是會員頭像，category 固定不開放呼叫端指定。zero-trust：直接用
 * userJwtDecoder 驗證的 Jwt 取 sub 當 memberId（比照 tengan-wallet CustomerWalletController 的
 * memberId(jwt) 慣例），不信任 Gateway 轉發的任何明文身份資訊——用來把每個會員的頭像各自存進
 * avatar/{memberId}/ 底下，避免全部會員的頭像扁平塞在同一個資料夾。
 */
@RestController
@RequestMapping("/api/customer/media")
public class CustomerMediaController {

    private static final String CATEGORY = "avatar";

    private final UploadImageUseCase uploadImageUseCase;

    public CustomerMediaController(UploadImageUseCase uploadImageUseCase) {
        this.uploadImageUseCase = uploadImageUseCase;
    }

    @PostMapping("/images")
    public UploadImageResponse uploadImage(@RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal Jwt jwt) {
        try {
            var result = uploadImageUseCase.upload(new UploadImageCommand(file.getBytes(),
                    file.getOriginalFilename(), file.getContentType(), CATEGORY, memberId(jwt)));
            return new UploadImageResponse(result.url());
        } catch (IOException e) {
            throw new UncheckedIOException("讀取上傳檔案失敗", e);
        }
    }

    private Long memberId(Jwt jwt) {
        return Long.valueOf(jwt.getSubject());
    }
}
