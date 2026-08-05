package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.exception.SkuNotFoundException;
import com.tengan.mall.product.domain.model.SpuStatus;
import org.springframework.stereotype.Service;

/**
 * 前台用：跟 GetPublicSpuDetailService 一樣，只回傳所屬 Spu 是 ON_SHELF 的 Sku，NEW/OFF_SHELF
 * 一律當「找不到」隱藏其存在——用 SpuStatusPort 做輕量狀態檢查，不因此讓 SkuDetailPort 本身背上
 * 上架限制（那個 port 之後也要給不該被此限制影響的內部高頻查詢用）。
 */
@Service
public class GetPublicSkuDetailService implements GetPublicSkuDetailUseCase {

    private final SkuDetailPort skuDetailPort;
    private final SpuStatusPort spuStatusPort;

    public GetPublicSkuDetailService(SkuDetailPort skuDetailPort, SpuStatusPort spuStatusPort) {
        this.skuDetailPort = skuDetailPort;
        this.spuStatusPort = spuStatusPort;
    }

    @Override
    public SkuDetailView get(Long skuId) {
        var view = skuDetailPort.findById(skuId).orElseThrow(() -> new SkuNotFoundException(skuId));
        var status = spuStatusPort.findStatus(view.spuId()).orElseThrow(() -> new SkuNotFoundException(skuId));
        if (status != SpuStatus.ON_SHELF) {
            throw new SkuNotFoundException(skuId);
        }
        return view;
    }
}
