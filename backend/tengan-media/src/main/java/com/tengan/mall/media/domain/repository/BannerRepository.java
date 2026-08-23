package com.tengan.mall.media.domain.repository;

import com.tengan.mall.media.domain.model.Banner;
import java.util.List;
import java.util.Optional;

public interface BannerRepository {

    Banner save(Banner banner);

    void update(Banner banner);

    void delete(Long id);

    Optional<Banner> findById(Long id);

    /** 依 sortOrder 排序，全部（含停用）——供後台管理頁使用。 */
    List<Banner> findAll();

    /** 依 sortOrder 排序，只回 enabled=true——供首頁公開端點使用。 */
    List<Banner> findAllEnabled();
}
