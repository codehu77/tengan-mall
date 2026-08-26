package com.tengan.mall.inventory.application.gate;

public interface GetGateStatusUseCase {

    GateConfigView get(Long skuId);
}
