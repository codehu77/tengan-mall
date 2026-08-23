package com.tengan.mall.media.application.banner;

import com.tengan.mall.media.domain.exception.BannerNotFoundException;
import com.tengan.mall.media.domain.model.Banner;
import com.tengan.mall.media.domain.repository.BannerRepository;
import org.springframework.stereotype.Service;

@Service
public class UpdateBannerService implements UpdateBannerUseCase {

    private final BannerRepository bannerRepository;

    public UpdateBannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Override
    public void update(UpdateBannerCommand command) {
        Banner banner = bannerRepository.findById(command.id())
                .orElseThrow(() -> new BannerNotFoundException(command.id()));
        banner.update(command.imageUrl(), command.linkUrl(), command.title(), command.sortOrder(),
                command.enabled());
        bannerRepository.update(banner);
    }
}
