package com.tengan.mall.media.infrastructure.persistence;

import com.tengan.mall.media.domain.model.MediaAsset;
import com.tengan.mall.media.domain.model.MediaAssetStatus;
import com.tengan.mall.media.domain.repository.MediaAssetRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public class MediaAssetRepositoryImpl implements MediaAssetRepository {

    private final MediaAssetMapper mapper;

    public MediaAssetRepositoryImpl(MediaAssetMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public MediaAsset save(MediaAsset asset) {
        MediaAssetPO po = new MediaAssetPO();
        toPO(asset, po);
        mapper.insert(po);
        asset.assignId(po.getId());
        return asset;
    }

    @Override
    public void update(MediaAsset asset) {
        MediaAssetPO po = new MediaAssetPO();
        po.setId(asset.getId());
        toPO(asset, po);
        mapper.updateById(po);
    }

    @Override
    public void delete(Long id) {
        mapper.deleteById(id);
    }

    @Override
    public List<MediaAsset> findByUrlIn(List<String> urls) {
        if (urls.isEmpty()) {
            return List.of();
        }
        return mapper.findByUrlIn(urls).stream().map(this::toDomain).toList();
    }

    @Override
    public List<MediaAsset> findConfirmedByOwner(String ownerType, Long ownerId) {
        return mapper.findConfirmedByOwner(ownerType, ownerId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<MediaAsset> findExpiredPending(LocalDateTime cutoff, int limit) {
        return mapper.findExpiredPending(cutoff, limit).stream().map(this::toDomain).toList();
    }

    private void toPO(MediaAsset asset, MediaAssetPO po) {
        po.setObjectKey(asset.getObjectKey());
        po.setUrl(asset.getUrl());
        po.setOwnerType(asset.getOwnerType());
        po.setOwnerId(asset.getOwnerId());
        po.setStatus(asset.getStatus().code());
    }

    private MediaAsset toDomain(MediaAssetPO po) {
        return MediaAsset.reconstitute(po.getId(), po.getObjectKey(), po.getUrl(), po.getOwnerType(),
                po.getOwnerId(), MediaAssetStatus.fromCode(po.getStatus()));
    }
}
