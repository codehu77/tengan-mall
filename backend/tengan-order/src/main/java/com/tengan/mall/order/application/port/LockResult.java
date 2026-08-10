package com.tengan.mall.order.application.port;

import java.util.List;

public record LockResult(boolean success, List<Long> shortageSkuIds) {
}
