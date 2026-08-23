package com.tengan.mall.media.domain.exception;

public class BannerNotFoundException extends RuntimeException {

    public BannerNotFoundException(Long bannerId) {
        super("找不到輪播圖: bannerId=" + bannerId);
    }
}
