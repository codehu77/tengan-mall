package com.tengan.mall.media.domain.model;

/**
 * 一個 MinIO 物件的生命週期追蹤列，不是聚合根意義下的「業務實體」——沒有豐富的不變條件，純粹記錄
 * 「這個物件現在是否有人宣告在用」。owner_type/owner_id 只有 CONFIRMED 時才有意義；PENDING 時可能
 * 是「剛上傳還沒人確認」也可能是「曾經被確認、後來被換掉」，兩種情況 GC 一視同仁回收，不特別區分。
 */
public class MediaAsset {

    private Long id;
    private final String objectKey;
    private final String url;
    private String ownerType;
    private Long ownerId;
    private MediaAssetStatus status;

    private MediaAsset(Long id, String objectKey, String url, String ownerType, Long ownerId,
            MediaAssetStatus status) {
        this.id = id;
        this.objectKey = objectKey;
        this.url = url;
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.status = status;
    }

    public static MediaAsset uploaded(String objectKey, String url) {
        return new MediaAsset(null, objectKey, url, null, null, MediaAssetStatus.PENDING);
    }

    public static MediaAsset reconstitute(Long id, String objectKey, String url, String ownerType, Long ownerId,
            MediaAssetStatus status) {
        return new MediaAsset(id, objectKey, url, ownerType, ownerId, status);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("MediaAsset 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void confirm(String ownerType, Long ownerId) {
        this.ownerType = ownerType;
        this.ownerId = ownerId;
        this.status = MediaAssetStatus.CONFIRMED;
    }

    /** 保留 owner 欄位供稽核追溯，不清空——GC 只看 status，owner 是誰不影響回收判斷。 */
    public void release() {
        this.status = MediaAssetStatus.PENDING;
    }

    public Long getId() {
        return id;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public String getUrl() {
        return url;
    }

    public String getOwnerType() {
        return ownerType;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public MediaAssetStatus getStatus() {
        return status;
    }
}
