package com.tengan.mall.media.application.banner;

import com.tengan.mall.media.domain.repository.BannerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListActiveBannersService implements ListActiveBannersUseCase {

    private final BannerRepository bannerRepository;

    public ListActiveBannersService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Override
    public List<PublicBannerView> list() {
        return bannerRepository.findAllEnabled().stream()
                .map(b -> new PublicBannerView(b.getId(), b.getImageUrl(), b.getLinkUrl(), b.getTitle()))
                .toList();
    }
}
