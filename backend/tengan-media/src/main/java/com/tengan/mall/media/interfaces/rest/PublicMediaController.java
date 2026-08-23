package com.tengan.mall.media.interfaces.rest;

import com.tengan.mall.media.application.banner.ListActiveBannersUseCase;
import com.tengan.mall.media.interfaces.rest.dto.PublicBannerListResponse;
import com.tengan.mall.media.interfaces.rest.dto.PublicBannerResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/media")
public class PublicMediaController {

    private final ListActiveBannersUseCase listActiveBannersUseCase;

    public PublicMediaController(ListActiveBannersUseCase listActiveBannersUseCase) {
        this.listActiveBannersUseCase = listActiveBannersUseCase;
    }

    @GetMapping("/banners")
    public PublicBannerListResponse listBanners() {
        var banners = listActiveBannersUseCase.list().stream()
                .map(b -> new PublicBannerResponse(b.id(), b.imageUrl(), b.linkUrl(), b.title()))
                .toList();
        return new PublicBannerListResponse(banners);
    }
}
