package com.tengan.mall.admin.infrastructure.product.dto;

import com.tengan.mall.admin.application.port.BaseAttrStandardValueItem;
import java.util.List;

public record BaseAttrStandardValueListEnvelope(List<BaseAttrStandardValueItem> items) {
}
