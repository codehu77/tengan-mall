package com.tengan.mall.media.domain.repository;

import com.tengan.mall.media.domain.model.MediaAsset;
import java.time.LocalDateTime;
import java.util.List;

public interface MediaAssetRepository {

    MediaAsset save(MediaAsset asset);

    void update(MediaAsset asset);

    void delete(Long id);

    /** 供 sync 用：這批 url 裡哪些是我們自己追蹤的物件（外部貼的網址天然不在裡面）。 */
    List<MediaAsset> findByUrlIn(List<String> urls);

    /** 供 sync 用：這個 owner 目前有哪些列是 CONFIRMED，用來跟新的 urls 取差集找出該被釋放的。 */
    List<MediaAsset> findConfirmedByOwner(String ownerType, Long ownerId);

    /** 供 GC 用：status=PENDING 且超過寬限期的候選，分批處理。 */
    List<MediaAsset> findExpiredPending(LocalDateTime cutoff, int limit);
}
