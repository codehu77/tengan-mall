package com.tengan.mall.admin.interfaces.rest;

import com.tengan.mall.admin.application.port.BannerPayload;
import com.tengan.mall.admin.application.port.BannerPort;
import com.tengan.mall.admin.interfaces.rest.dto.BannerListResponse;
import com.tengan.mall.admin.interfaces.rest.dto.BannerRequest;
import com.tengan.mall.admin.interfaces.rest.dto.BannerResponse;
import com.tengan.mall.admin.interfaces.rest.dto.CreateBannerResponse;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** BFF：轉發到 tengan-media 的 /internal/media/banners，純代理原則（同 SeckillSessionController）。 */
@RestController
@RequestMapping("/api/admin/media/banners")
public class BannerController {

    private final BannerPort bannerPort;

    public BannerController(BannerPort bannerPort) {
        this.bannerPort = bannerPort;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('media:banner:read')")
    public BannerListResponse listBanners() {
        var banners = bannerPort.listBanners().stream()
                .map(b -> new BannerResponse(b.id(), b.imageUrl(), b.linkUrl(), b.title(), b.sortOrder(),
                        b.enabled()))
                .toList();
        return new BannerListResponse(banners);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('media:banner:write')")
    public CreateBannerResponse createBanner(@Valid @RequestBody BannerRequest request) {
        Long id = bannerPort.createBanner(new BannerPayload(request.imageUrl(), request.linkUrl(), request.title(),
                request.sortOrder(), request.enabled()));
        return new CreateBannerResponse(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('media:banner:write')")
    public void updateBanner(@PathVariable Long id, @Valid @RequestBody BannerRequest request) {
        bannerPort.updateBanner(id, new BannerPayload(request.imageUrl(), request.linkUrl(), request.title(),
                request.sortOrder(), request.enabled()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('media:banner:write')")
    public void deleteBanner(@PathVariable Long id) {
        bannerPort.deleteBanner(id);
    }
}
