package com.tengan.mall.media.application.banner;

import com.tengan.mall.media.domain.repository.BannerRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ListBannersService implements ListBannersUseCase {

    private final BannerRepository bannerRepository;

    public ListBannersService(BannerRepository bannerRepository) {
        this.bannerRepository = bannerRepository;
    }

    @Override
    public List<BannerView> list() {
        return bannerRepository.findAll().stream()
                .map(b -> new BannerView(b.getId(), b.getImageUrl(), b.getLinkUrl(), b.getTitle(), b.getSortOrder(),
                        b.isEnabled()))
                .toList();
    }
}
