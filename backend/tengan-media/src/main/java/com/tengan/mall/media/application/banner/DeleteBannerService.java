package com.tengan.mall.media.application.banner;

import com.tengan.mall.media.domain.repository.BannerRepository;
import org.springframework.stereotype.Service;

@Service
public class DeleteBannerService implements DeleteBannerUseCase {

    private final BannerRepository bannerRepository;

    public DeleteBannerService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Override
    public void delete(Long id) {
        bannerRepository.delete(id);
    }
}
