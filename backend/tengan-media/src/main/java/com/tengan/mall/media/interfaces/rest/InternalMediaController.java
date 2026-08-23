package com.tengan.mall.media.interfaces.rest;

import com.tengan.mall.media.application.banner.CreateBannerCommand;
import com.tengan.mall.media.application.banner.CreateBannerUseCase;
import com.tengan.mall.media.application.banner.DeleteBannerUseCase;
import com.tengan.mall.media.application.banner.ListBannersUseCase;
import com.tengan.mall.media.application.banner.UpdateBannerCommand;
import com.tengan.mall.media.application.banner.UpdateBannerUseCase;
import com.tengan.mall.media.application.upload.DeleteObjectsCommand;
import com.tengan.mall.media.application.upload.DeleteObjectsUseCase;
import com.tengan.mall.media.application.upload.UploadImageCommand;
import com.tengan.mall.media.application.upload.UploadImageUseCase;
import com.tengan.mall.media.interfaces.rest.dto.BannerListResponse;
import com.tengan.mall.media.interfaces.rest.dto.BannerRequest;
import com.tengan.mall.media.interfaces.rest.dto.BannerResponse;
import com.tengan.mall.media.interfaces.rest.dto.CreateBannerResponse;
import com.tengan.mall.media.interfaces.rest.dto.DeleteObjectsRequest;
import com.tengan.mall.media.interfaces.rest.dto.UploadImageResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import java.io.UncheckedIOException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/** 供 tengan-admin BFF 轉發呼叫，scope 要求見 RegisteredClientSeeder 的 media.read/media.write。 */
@RestController
@RequestMapping("/internal/media")
public class InternalMediaController {

    private final ListBannersUseCase listBannersUseCase;
    private final CreateBannerUseCase createBannerUseCase;
    private final UpdateBannerUseCase updateBannerUseCase;
    private final DeleteBannerUseCase deleteBannerUseCase;
    private final UploadImageUseCase uploadImageUseCase;
    private final DeleteObjectsUseCase deleteObjectsUseCase;

    public InternalMediaController(ListBannersUseCase listBannersUseCase, CreateBannerUseCase createBannerUseCase,
            UpdateBannerUseCase updateBannerUseCase, DeleteBannerUseCase deleteBannerUseCase,
            UploadImageUseCase uploadImageUseCase, DeleteObjectsUseCase deleteObjectsUseCase) {
        this.listBannersUseCase = listBannersUseCase;
        this.createBannerUseCase = createBannerUseCase;
        this.updateBannerUseCase = updateBannerUseCase;
        this.deleteBannerUseCase = deleteBannerUseCase;
        this.uploadImageUseCase = uploadImageUseCase;
        this.deleteObjectsUseCase = deleteObjectsUseCase;
    }

    @GetMapping("/banners")
    @PreAuthorize("hasAuthority('SCOPE_media.read')")
    public BannerListResponse listBanners() {
        var banners = listBannersUseCase.list().stream()
                .map(b -> new BannerResponse(b.id(), b.imageUrl(), b.linkUrl(), b.title(), b.sortOrder(),
                        b.enabled()))
                .toList();
        return new BannerListResponse(banners);
    }

    @PostMapping("/banners")
    @PreAuthorize("hasAuthority('SCOPE_media.write')")
    public CreateBannerResponse createBanner(@Valid @RequestBody BannerRequest request) {
        Long id = createBannerUseCase.create(new CreateBannerCommand(request.imageUrl(), request.linkUrl(),
                request.title(), request.sortOrder(), request.enabled()));
        return new CreateBannerResponse(id);
    }

    @PutMapping("/banners/{id}")
    @PreAuthorize("hasAuthority('SCOPE_media.write')")
    public ResponseEntity<Void> updateBanner(@PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        updateBannerUseCase.update(new UpdateBannerCommand(id, request.imageUrl(), request.linkUrl(),
                request.title(), request.sortOrder(), request.enabled()));
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/banners/{id}")
    @PreAuthorize("hasAuthority('SCOPE_media.write')")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        deleteBannerUseCase.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * category 限 banner/product/admin-avatar——後台素材上傳+管理員自己的頭像，跟顧客頭像的
     * avatar 分開命名空間。ownerId 選填，呼叫端想把同一 category 底下的檔案依擁有者分資料夾
     * （目前是 tengan-admin 轉發管理員頭像上傳時會帶）才需要傳，banner/product 沒有擁有者概念不用傳。
     */
    @PostMapping("/images")
    @PreAuthorize("hasAuthority('SCOPE_media.write')")
    public UploadImageResponse uploadImage(@RequestParam("file") MultipartFile file,
            @RequestParam("category") String category,
            @RequestParam(value = "ownerId", required = false) Long ownerId) {
        try {
            var result = uploadImageUseCase.upload(new UploadImageCommand(file.getBytes(),
                    file.getOriginalFilename(), file.getContentType(), category, ownerId));
            return new UploadImageResponse(result.url());
        } catch (IOException e) {
            throw new UncheckedIOException("讀取上傳檔案失敗", e);
        }
    }

    /** best-effort 批次刪除——供 tengan-product 刪除 SPU 時一次清掉該商品所有圖片使用。 */
    @DeleteMapping("/objects")
    @PreAuthorize("hasAuthority('SCOPE_media.write')")
    public ResponseEntity<Void> deleteObjects(@RequestBody DeleteObjectsRequest request) {
        deleteObjectsUseCase.delete(new DeleteObjectsCommand(request.urls()));
        return ResponseEntity.noContent().build();
    }
}
