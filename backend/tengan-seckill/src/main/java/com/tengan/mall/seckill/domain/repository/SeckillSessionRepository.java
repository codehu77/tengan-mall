package com.tengan.mall.seckill.domain.repository;

import com.tengan.mall.seckill.domain.model.SeckillSession;
import java.util.List;
import java.util.Optional;

public interface SeckillSessionRepository {

    SeckillSession save(SeckillSession session);

    void update(SeckillSession session);

    void delete(Long id);

    Optional<SeckillSession> findById(Long id);

    /** 依 sortOrder/timeOfDay 排序，供後台場次管理頁+建活動選單使用。 */
    List<SeckillSession> findAll();
}
