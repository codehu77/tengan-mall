package com.tengan.mall.seckill.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDateTime;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SeckillActivityMapper extends BaseMapper<SeckillActivityPO> {

    @Select("SELECT * FROM seckill_activity WHERE status = 2 AND start_time <= #{horizon} AND end_time > #{now} "
            + "ORDER BY start_time ASC")
    List<SeckillActivityPO> findReadyToWarmUp(@Param("now") LocalDateTime now, @Param("horizon") LocalDateTime horizon);

    @Select("SELECT * FROM seckill_activity WHERE status = 3 AND end_time <= #{cutoff} ORDER BY end_time ASC")
    List<SeckillActivityPO> findActiveEndedBefore(@Param("cutoff") LocalDateTime cutoff);
}
