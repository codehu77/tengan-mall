package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.upload.UploadFileCommand;
import com.tengan.mall.admin.application.upload.UploadFileUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.UploadFileResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 管理員自己的頭像上傳，category="admin-avatar"、依 adminId 分資料夾，跟會員/後台素材分開命名空間。 */
@RestController
@RequestMapping("/api/admin/files")
public class FileUploadController {

    private final UploadFileUseCase uploadFileUseCase;

    public FileUploadController(UploadFileUseCase uploadFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @PostMapping("/upload")
    public UploadFileResponse upload(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal Jwt adminJwt) {
        try {
            Long adminId = Long.valueOf(adminJwt.getSubject());
            var result = uploadFileUseCase.upload(new UploadFileCommand(file.getBytes(), file.getOriginalFilename(),
                    file.getContentType(), "admin-avatar", adminId));
            return new UploadFileResponse(result.url());
        } catch (IOException e) {
            throw new UncheckedIOException("讀取上傳檔案失敗", e);
        }
    }
}
