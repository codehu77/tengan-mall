package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.upload.UploadFileCommand;
import com.tengan.mall.admin.application.upload.UploadFileUseCase;
import com.tengan.mall.admin.interfaces.rest.dto.UploadFileResponse;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin/files")
public class FileUploadController {

    private final UploadFileUseCase uploadFileUseCase;

    public FileUploadController(UploadFileUseCase uploadFileUseCase) {
        this.uploadFileUseCase = uploadFileUseCase;
    }

    @PostMapping("/upload")
    public UploadFileResponse upload(@RequestParam("file") MultipartFile file) {
        try {
            var result = uploadFileUseCase.upload(new UploadFileCommand(file.getBytes(), file.getOriginalFilename(),
                    file.getContentType(), "avatar"));
            return new UploadFileResponse(result.url());
        } catch (IOException e) {
            throw new UncheckedIOException("讀取上傳檔案失敗", e);
        }
    }
}
