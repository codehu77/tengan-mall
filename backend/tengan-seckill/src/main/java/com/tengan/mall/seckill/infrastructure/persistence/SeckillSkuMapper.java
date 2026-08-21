package com.tengan.mall.seckill.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface SeckillSkuMapper extends BaseMapper<SeckillSkuPO> {

    @Update("UPDATE seckill_sku SET sold_count = #{soldCount}, settled_at = #{settledAt} "
            + "WHERE id = #{id} AND settled_at IS NULL")
    int settle(@Param("id") Long id, @Param("soldCount") int soldCount, @Param("settledAt") LocalDateTime settledAt);
}
