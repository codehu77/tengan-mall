package com.tengan.mall.media.application.lifecycle;

import java.util.List;

public interface SyncOwnerMediaAssetsUseCase {

    /**
     * 宣告 owner 現在完整引用哪些 url（不是增量 attach/detach）——傳進來的視為「目前真正在用」，
     * 之前 CONFIRMED、這次沒出現的視為「被換掉了」。刪除整個 owner 時傳空 list 即可，等同於
     * 「這個 owner 現在什麼都不用」。
     */
    void sync(String ownerType, Long ownerId, List<String> urls);
}
