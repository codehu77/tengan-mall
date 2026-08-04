package com.tengan.mall.admin.infrastructure.product.dto;

import com.tengan.mall.admin.application.port.BaseAttrGroupItem;
import java.util.List;

public record BaseAttrGroupListEnvelope(List<BaseAttrGroupItem> items) {
}
