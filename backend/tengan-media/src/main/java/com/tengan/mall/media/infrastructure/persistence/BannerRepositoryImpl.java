package com.tengan.mall.media.infrastructure.persistence;

import com.tengan.mall.media.domain.model.Banner;
import com.tengan.mall.media.domain.repository.BannerRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BannerRepositoryImpl implements BannerRepository {

    private final BannerMapper mapper;

    public BannerRepositoryImpl(BannerMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public Banner save(Banner banner) {
        BannerPO po = new BannerPO();
        toPO(banner, po);
        mapper.insert(po);
        banner.assignId(po.getId());
        return banner;
    }

    @Override
    public void update(Banner banner) {
        BannerPO po = new BannerPO();
        po.setId(banner.getId());
        toPO(banner, po);
        mapper.updateById(po);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public Optional<Banner> findById(Long id) {
        return Optional.ofNullable(mapper.selectById(id)).map(this::toDomain);
    }

    @Override
    public List<Banner> findAll() {
        return mapper.findAllOrdered().stream().map(this::toDomain).toList();
    }

    @Override
    public List<Banner> findAllEnabled() {
        return mapper.findAllEnabledOrdered().stream().map(this::toDomain).toList();
    }

    private void toPO(Banner banner, BannerPO po) {
        po.setImageUrl(banner.getImageUrl());
        po.setLinkUrl(banner.getLinkUrl());
        po.setTitle(banner.getTitle());
        po.setSortOrder(banner.getSortOrder());
        po.setEnabled(banner.isEnabled());
    }

    private Banner toDomain(BannerPO po) {
        return Banner.reconstitute(po.getId(), po.getImageUrl(), po.getLinkUrl(), po.getTitle(), po.getSortOrder(),
                Boolean.TRUE.equals(po.getEnabled()));
    }
}
