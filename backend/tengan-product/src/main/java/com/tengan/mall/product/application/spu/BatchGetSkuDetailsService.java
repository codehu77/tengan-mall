package com.tengan.mall.product.application.spu;

import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 給 tengan-cart 這種 internal 呼叫端用：不套上架過濾（見 SkuDetailPort 的註解），下架商品仍能查到
 * 目前價格/名稱，交由呼叫端自行決定怎麼呈現（不是這個服務的職責）。
 */
@Service
public class BatchGetSkuDetailsService implements BatchGetSkuDetailsUseCase {

    private final SkuDetailPort skuDetailPort;

    public BatchGetSkuDetailsService(SkuDetailPort skuDetailPort) {
        this.skuDetailPort = skuDetailPort;
    }

    @Override
    public List<SkuDetailView> get(List<Long> skuIds) {
        return skuDetailPort.findByIds(skuIds);
    }
}
