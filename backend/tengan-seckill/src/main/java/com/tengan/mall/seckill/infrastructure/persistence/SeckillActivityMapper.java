package com.tengan.mall.seckill.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDate;
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

    @Select("SELECT * FROM seckill_activity WHERE status = 3 ORDER BY start_time ASC")
    List<SeckillActivityPO> findActive();

    /** activity_type=1(FLASH_SALE) 且 status IN (2=PUBLISHED, 3=ACTIVE)——今天所有場次（含待開賣的），供公開展示端點的多場次分頁用。 */
    @Select("SELECT * FROM seckill_activity WHERE activity_type = 1 AND activity_date = #{date} "
            + "AND status IN (2, 3) ORDER BY start_time ASC")
    List<SeckillActivityPO> findFlashSaleSessionsOnDate(@Param("date") LocalDate date);
}
