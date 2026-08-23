package com.tengan.mall.media.application.banner;

import com.tengan.mall.media.domain.model.Banner;
import com.tengan.mall.media.domain.repository.BannerRepository;
import org.springframework.stereotype.Service;

@Service
public class CreateBannerService implements CreateBannerUseCase {

    private final BannerRepository bannerRepository;

    public CreateBannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Override
    public Long create(CreateBannerCommand command) {
        Banner banner = Banner.create(command.imageUrl(), command.linkUrl(), command.title(), command.sortOrder(),
                command.enabled());
        return bannerRepository.save(banner).getId();
    }
}
