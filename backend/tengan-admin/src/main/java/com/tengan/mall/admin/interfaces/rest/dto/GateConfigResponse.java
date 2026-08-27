package com.tengan.mall.admin.interfaces.rest.dto;

import java.time.LocalDateTime;

public record GateConfigResponse(Long spuId, LocalDateTime gateWarmedAt, LocalDateTime gateCloseTime) {
}
