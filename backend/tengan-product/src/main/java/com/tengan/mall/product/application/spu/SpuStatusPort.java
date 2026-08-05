package com.tengan.mall.product.application.spu;

import com.tengan.mall.product.domain.model.SpuStatus;
import java.util.Optional;

/**
 * 跟 SkuDetailPort 同一種 CQRS-lite 精神：只查 spu 表單一欄位（status），不經過 SpuRepository
 * 載入整個聚合根（含所有兄弟 sku）。專門給 GetPublicSkuDetailService 這種「單筆 sku 查詢也要擋
 * 非上架 spu」的情境用，不能直接複用 SkuDetailPort.findById（那個 port 之後要給 cart/order
 * 這種不該被上架狀態限制的內部高頻查詢用，見該 port 的註解）。
 */
public interface SpuStatusPort {

    Optional<SpuStatus> findStatus(Long spuId);
}
