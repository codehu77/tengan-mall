package com.tengan.mall.cart.application.cart;

import java.util.List;
import java.util.Optional;

/**
 * 訪客購物車走 Redis，短 TTL 即用即棄，不套用 CartItemRepository 那種聚合根 Repository 介面
 * （見 cart_storage_decision：訪客購物車沒有需要保護的業務規則，純暫存資料）。
 */
public interface GuestCartPort {

    List<GuestCartItem> findAll(String guestKey);

    Optional<GuestCartItem> find(String guestKey, Long skuId);

    /** upsert 單一項目（新增或覆蓋），並刷新整個 guestKey 的 TTL。 */
    void save(String guestKey, GuestCartItem item);

    void remove(String guestKey, Long skuId);

    void removeChecked(String guestKey);

    void setCheckedAll(String guestKey, boolean checked);

    void deleteAll(String guestKey);
}
