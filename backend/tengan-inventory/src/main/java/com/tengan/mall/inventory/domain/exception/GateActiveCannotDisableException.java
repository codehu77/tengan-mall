package com.tengan.mall.inventory.domain.exception;

/** 閘門正在保護中（已預熱、尚未關閉）時不能停用，避免 Redis 還有進行中的保留卻突然切回 MySQL 路徑造成庫存不一致。 */
public class GateActiveCannotDisableException extends RuntimeException {

    public GateActiveCannotDisableException(Long skuId) {
        super("庫存流量閘門正在保護中，請等待關閉時間到、結算完成後再停用: skuId=" + skuId);
    }
}
