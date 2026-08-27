package com.tengan.mall.inventory.application.gate;

import java.time.LocalDateTime;

public interface ConfigureGateUseCase {

    void configure(Long skuId, LocalDateTime gateCloseTime);
}
