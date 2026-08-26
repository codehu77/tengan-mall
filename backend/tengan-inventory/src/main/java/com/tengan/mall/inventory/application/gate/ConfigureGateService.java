package com.tengan.mall.inventory.application.gate;

import com.tengan.mall.inventory.domain.exception.GateActiveCannotDisableException;
import com.tengan.mall.inventory.domain.exception.InvalidGateCloseTimeException;
import com.tengan.mall.inventory.domain.exception.NoStockForGateException;
import com.tengan.mall.inventory.domain.exception.SaleStartTimeNotSetException;
import com.tengan.mall.inventory.domain.exception.SkuLaunchConfigNotFoundException;
import com.tengan.mall.inventory.domain.repository.SkuLaunchConfigRepository;
import com.tengan.mall.inventory.domain.repository.WareSkuRepository;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

/** 管理員在庫存頁面直接設定庫存流量閘門用，見 plan「庫存流量閘門搬出 SPU 表單」的驗證順序。 */
@Service
public class ConfigureGateService implements ConfigureGateUseCase {

    private final SkuLaunchConfigRepository skuLaunchConfigRepository;
    private final WareSkuRepository wareSkuRepository;

    public ConfigureGateService(SkuLaunchConfigRepository skuLaunchConfigRepository,
            WareSkuRepository wareSkuRepository) {
        this.skuLaunchConfigRepository = skuLaunchConfigRepository;
        this.wareSkuRepository = wareSkuRepository;
    }

    @Override
    public void configure(Long skuId, boolean trafficGateEnabled, LocalDateTime gateCloseTime) {
        var config = skuLaunchConfigRepository.findBySkuId(skuId)
                .orElseThrow(() -> new SkuLaunchConfigNotFoundException(skuId));

        if (trafficGateEnabled) {
            if (config.saleStartTime() == null) {
                throw new SaleStartTimeNotSetException(skuId);
            }
            if (gateCloseTime == null || !gateCloseTime.isAfter(config.saleStartTime())) {
                throw new InvalidGateCloseTimeException(skuId);
            }
            if (wareSkuRepository.sumAvailableStock(skuId) <= 0) {
                throw new NoStockForGateException(skuId);
            }
        } else if (config.isGateActive(LocalDateTime.now())) {
            throw new GateActiveCannotDisableException(skuId);
        }

        skuLaunchConfigRepository.configureGate(skuId, trafficGateEnabled, trafficGateEnabled ? gateCloseTime : null);
    }
}
