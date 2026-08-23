package com.tengan.mall.admin.application.port;

import java.util.List;

/** 呼叫 tengan-media 的 Banner CRUD internal 端點，純代理原則（同 SeckillSessionPort）。 */
public interface BannerPort {

    List<BannerItem> listBanners();

    Long createBanner(BannerPayload payload);

    void updateBanner(Long id, BannerPayload payload);

    void deleteBanner(Long id);
}
