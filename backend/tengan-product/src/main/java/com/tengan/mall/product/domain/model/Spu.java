package com.tengan.mall.product.domain.model;

import com.tengan.mall.product.domain.exception.InvalidGateCloseTimeException;
import com.tengan.mall.product.domain.exception.InvalidTeaserRemoveTimeException;
import com.tengan.mall.product.domain.exception.SpuHasNoSkuException;
import com.tengan.mall.product.domain.exception.SpuIsDuplicateException;
import com.tengan.mall.product.domain.exception.SpuNotOnShelfException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 聚合根：商品(SPU)，Sku 是內部 child entity，不是獨立聚合根——上架(publish)有「底下至少要有一顆
 * Sku」這個不變條件，需要交易一致性保護（同一個 transaction 存檔，不會出現檢查完、存檔前 Sku 被刪光
 * 的時間窗口），這是判斷該合併成一個聚合根的訊號。前台高頻讀取（GET /skus/{skuId}）不經過這個聚合根，
 * 走獨立的 SkuDetailPort 直接查，所以合併付出的代價只在低頻的後台寫入路徑。
 */
public class Spu {

    private Long id;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String description;
    private String mainImage;
    private SpuStatus status;
    private LocalDateTime saleStartTime;
    private boolean trafficGateEnabled;
    private LocalDateTime gateCloseTime;
    private boolean showOnLaunchTeaser;
    private LocalDateTime teaserRemoveAt;
    private final List<Sku> skus = new ArrayList<>();
    private final List<SpuBaseAttrValue> attrValues = new ArrayList<>();
    private final List<SpuImage> images = new ArrayList<>();

    private Spu(Long id, Long categoryId, Long brandId, String name, String description, String mainImage,
            SpuStatus status, LocalDateTime saleStartTime, boolean trafficGateEnabled, LocalDateTime gateCloseTime,
            boolean showOnLaunchTeaser, LocalDateTime teaserRemoveAt) {
        this.id = id;
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.mainImage = mainImage;
        this.status = status;
        this.saleStartTime = saleStartTime;
        this.trafficGateEnabled = trafficGateEnabled;
        this.gateCloseTime = gateCloseTime;
        this.showOnLaunchTeaser = showOnLaunchTeaser;
        this.teaserRemoveAt = teaserRemoveAt;
    }

    /** 一般商品建立時不用管「即將開賣」排程，全部留關閉/null，之後可用 scheduleLaunch() 補設。 */
    public static Spu create(Long categoryId, Long brandId, String name, String description, String mainImage) {
        return new Spu(null, categoryId, brandId, name, description, mainImage, SpuStatus.NEW, null, false, null,
                false, null);
    }

    /**
     * 複製一份現有 Spu 當草稿——規格參數/共通圖片/Sku 清單（含其專屬圖片、銷售屬性值、限購數量）整份帶過去，
     * 價格照抄原值，管理員後續自己調整；Sku 銷量重置為 0（新商品不該繼承原品的假業績）。狀態固定是
     * DUPLICATE，不是 NEW：必須先被人工編輯過一次（見 updateBasicInfo）才能上架，方便稽核複製出來的
     * 草稿是否真的被檢查過，不是複製完忘記調價/改名就直接發布。
     *
     * <p>開賣時間/流量閘門/首頁預告這組「上市排程」欄位刻意不複製——複製出來的草稿不該直接繼承別的商品
     * 的上市排程，一律回到關閉/null，管理員要另外設定。</p>
     */
    public static Spu duplicateOf(Spu source, String newName) {
        Spu spu = new Spu(null, source.categoryId, source.brandId, newName, source.description, source.mainImage,
                SpuStatus.DUPLICATE, null, false, null, false, null);
        spu.attrValues.addAll(source.attrValues);
        spu.images.addAll(source.images);
        spu.skus.addAll(source.skus.stream()
                .map(s -> Sku.create(s.getName(), s.getPrice(), s.getMainImage(), s.getSort(), s.getImages(),
                        s.getSaleAttrValues(), s.getPurchaseLimitPerUser()))
                .toList());
        return spu;
    }

    public static Spu reconstitute(Long id, Long categoryId, Long brandId, String name, String description,
            String mainImage, SpuStatus status, LocalDateTime saleStartTime, boolean trafficGateEnabled,
            LocalDateTime gateCloseTime, boolean showOnLaunchTeaser, LocalDateTime teaserRemoveAt, List<Sku> skus,
            List<SpuBaseAttrValue> attrValues, List<SpuImage> images) {
        Spu spu = new Spu(id, categoryId, brandId, name, description, mainImage, status, saleStartTime,
                trafficGateEnabled, gateCloseTime, showOnLaunchTeaser, teaserRemoveAt);
        spu.skus.addAll(skus);
        spu.attrValues.addAll(attrValues);
        spu.images.addAll(images);
        return spu;
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Spu 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void updateBasicInfo(Long categoryId, Long brandId, String name, String description, String mainImage) {
        this.categoryId = categoryId;
        this.brandId = brandId;
        this.name = name;
        this.description = description;
        this.mainImage = mainImage;
        // 複製草稿被編輯過一次代表管理員真的看過、確認過內容，解除「不能直接上架」的限制。
        if (status == SpuStatus.DUPLICATE) {
            status = SpuStatus.NEW;
        }
    }

    /**
     * 設定「即將開賣」排程：開賣時間、流量閘門（開賣瞬間的保護機制，Phase A 只存欄位不做 Redis 保護）、
     * 首頁預告顯示。trafficGateEnabled=true 時 gateCloseTime 必須晚於 saleStartTime——閘門關閉時間
     * 是「保護期結束、轉一般路徑」的時間點，不能早於或等於開賣本身。showOnLaunchTeaser=true 時
     * teaserRemoveAt 必須晚於 saleStartTime——預告下架時間的用意是讓已經開賣一陣子的商品仍留在首頁
     * 顯示「熱門搶購中」，如果比開賣時間還早就沒有意義。
     */
    public void scheduleLaunch(LocalDateTime saleStartTime, boolean trafficGateEnabled, LocalDateTime gateCloseTime,
            boolean showOnLaunchTeaser, LocalDateTime teaserRemoveAt) {
        if (trafficGateEnabled && (gateCloseTime == null || saleStartTime == null
                || !gateCloseTime.isAfter(saleStartTime))) {
            throw new InvalidGateCloseTimeException(id);
        }
        if (showOnLaunchTeaser && (teaserRemoveAt == null || saleStartTime == null
                || !teaserRemoveAt.isAfter(saleStartTime))) {
            throw new InvalidTeaserRemoveTimeException(id);
        }
        this.saleStartTime = saleStartTime;
        this.trafficGateEnabled = trafficGateEnabled;
        this.gateCloseTime = gateCloseTime;
        this.showOnLaunchTeaser = showOnLaunchTeaser;
        this.teaserRemoveAt = teaserRemoveAt;
    }

    /** 整批替換底下的 Sku 集合，差異寫入（新增 insert、既有 update、被移除的 delete）交給 Repository 處理。 */
    public void replaceSkus(List<Sku> newSkus) {
        this.skus.clear();
        this.skus.addAll(newSkus);
    }

    public void replaceAttrValues(List<SpuBaseAttrValue> newAttrValues) {
        this.attrValues.clear();
        this.attrValues.addAll(newAttrValues);
    }

    public void replaceImages(List<SpuImage> newImages) {
        this.images.clear();
        this.images.addAll(newImages);
    }

    public void publish() {
        if (status == SpuStatus.DUPLICATE) {
            throw new SpuIsDuplicateException(id);
        }
        if (skus.isEmpty()) {
            throw new SpuHasNoSkuException(id);
        }
        this.status = SpuStatus.ON_SHELF;
    }

    public void unlist() {
        if (status != SpuStatus.ON_SHELF) {
            throw new SpuNotOnShelfException(id);
        }
        this.status = SpuStatus.OFF_SHELF;
    }

    public Long getId() {
        return id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getMainImage() {
        return mainImage;
    }

    public SpuStatus getStatus() {
        return status;
    }

    public LocalDateTime getSaleStartTime() {
        return saleStartTime;
    }

    public boolean isTrafficGateEnabled() {
        return trafficGateEnabled;
    }

    public LocalDateTime getGateCloseTime() {
        return gateCloseTime;
    }

    public boolean isShowOnLaunchTeaser() {
        return showOnLaunchTeaser;
    }

    public LocalDateTime getTeaserRemoveAt() {
        return teaserRemoveAt;
    }

    public List<Sku> getSkus() {
        return Collections.unmodifiableList(skus);
    }

    public List<SpuBaseAttrValue> getAttrValues() {
        return Collections.unmodifiableList(attrValues);
    }

    public List<SpuImage> getImages() {
        return Collections.unmodifiableList(images);
    }
}
