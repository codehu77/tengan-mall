package com.tengan.mall.admin.infrastructure.order.dto;

import com.tengan.mall.admin.application.port.RevenueTrendPoint;
import java.util.List;

public record RevenueTrendEnvelope(List<RevenueTrendPoint> points) {
}
