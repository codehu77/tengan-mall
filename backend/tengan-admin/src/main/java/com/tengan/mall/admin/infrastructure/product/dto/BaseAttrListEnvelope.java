package com.tengan.mall.admin.infrastructure.product.dto;

import com.tengan.mall.admin.application.port.BaseAttrItem;
import java.util.List;

public record BaseAttrListEnvelope(List<BaseAttrItem> items) {
}
