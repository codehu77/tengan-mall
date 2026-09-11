package com.tengan.mall.media.domain.model;

public enum MediaAssetStatus {

    /** 上傳完但還沒有任何 owner 透過 sync 確認使用中，或曾經 CONFIRMED 後被換掉——GC 回收候選。 */
    PENDING(1),
    /** 目前有 owner 正在引用（最近一次 sync 有帶到這個 url）。 */
    CONFIRMED(2);

    private final int code;

    MediaAssetStatus(int code) {
        this.code = code;
    }

    public int code() {
        return code;
    }

    public static MediaAssetStatus fromCode(int code) {
        for (MediaAssetStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的 MediaAssetStatus code: " + code);
    }
}
