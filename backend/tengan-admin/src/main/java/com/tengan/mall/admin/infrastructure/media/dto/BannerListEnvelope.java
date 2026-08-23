package com.tengan.mall.admin.infrastructure.media.dto;

import com.tengan.mall.admin.application.port.BannerItem;
import java.util.List;

public record BannerListEnvelope(List<BannerItem> banners) {
}
