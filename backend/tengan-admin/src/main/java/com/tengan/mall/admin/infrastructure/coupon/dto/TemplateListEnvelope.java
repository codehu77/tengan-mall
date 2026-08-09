package com.tengan.mall.admin.infrastructure.coupon.dto;

import com.tengan.mall.admin.application.port.TemplateItem;
import java.util.List;

public record TemplateListEnvelope(List<TemplateItem> items) {
}
