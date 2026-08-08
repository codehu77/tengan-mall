package com.tengan.mall.cart.domain.model;

/**
 * 會員購物車項目：扁平聚合根（不是 Spu/Sku 那種 root+child），每個項目獨立增刪改，
 * 項目之間沒有需要原子保護的跨項目不變條件（見 cart_storage_decision）。
 *
 * <p>刻意不存價格快照——sku 可能放置數月，快照價格早已過期沒有意義，讀取時一律即時查
 * tengan-product 目前的價格（見 ProductSkuPort）。specText 例外：純粹是加入當下選定的規格
 * 顯示文字（例如「藏藍/1TB」），不是金額相關資料，快照沒有正確性疑慮，只是給使用者看的標籤。</p>
 */
public class CartItem {

    private Long id;
    private final Long userId;
    private final Long skuId;
    private int count;
    private boolean checked;
    private String specText;

    private CartItem(Long id, Long userId, Long skuId, int count, boolean checked, String specText) {
        this.id = id;
        this.userId = userId;
        this.skuId = skuId;
        this.count = count;
        this.checked = checked;
        this.specText = specText;
    }

    public static CartItem create(Long userId, Long skuId, int count, String specText) {
        requirePositiveCount(count);
        return new CartItem(null, userId, skuId, count, true, specText);
    }

    public static CartItem reconstitute(Long id, Long userId, Long skuId, int count, boolean checked,
            String specText) {
        return new CartItem(id, userId, skuId, count, checked, specText);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("CartItem 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void changeCount(int newCount) {
        requirePositiveCount(newCount);
        this.count = newCount;
    }

    public void increaseCount(int delta) {
        requirePositiveCount(delta);
        this.count += delta;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void updateSpecText(String specText) {
        this.specText = specText;
    }

    public boolean belongsTo(Long userId) {
        return this.userId.equals(userId);
    }

    private static void requirePositiveCount(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("購物車數量必須大於 0: " + count);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getSkuId() {
        return skuId;
    }

    public int getCount() {
        return count;
    }

    public boolean isChecked() {
        return checked;
    }

    public String getSpecText() {
        return specText;
    }
}
