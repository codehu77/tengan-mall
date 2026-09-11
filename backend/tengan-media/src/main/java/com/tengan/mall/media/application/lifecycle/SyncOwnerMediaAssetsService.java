package com.tengan.mall.media.application.lifecycle;

import com.tengan.mall.media.domain.repository.MediaAssetRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SyncOwnerMediaAssetsService implements SyncOwnerMediaAssetsUseCase {

    private final MediaAssetRepository mediaAssetRepository;

    public SyncOwnerMediaAssetsService(MediaAssetRepository mediaAssetRepository) {
        this.mediaAssetRepository = mediaAssetRepository;
    }

    @Override
    @Transactional
    public void sync(String ownerType, Long ownerId, List<String> urls) {
        Set<String> urlSet = new HashSet<>(urls);

        // 傳進來的 url 裡，找出我們自己追蹤的物件（外部貼的網址天然查不到，安靜略過）確認為使用中。
        for (var asset : mediaAssetRepository.findByUrlIn(urls)) {
            asset.confirm(ownerType, ownerId);
            mediaAssetRepository.update(asset);
        }

        // 這個 owner 之前 CONFIRMED、這次沒出現在新清單裡的，代表被換掉了，打回 PENDING 交給 GC。
        for (var asset : mediaAssetRepository.findConfirmedByOwner(ownerType, ownerId)) {
            if (!urlSet.contains(asset.getUrl())) {
                asset.release();
                mediaAssetRepository.update(asset);
            }
        }
    }
}
