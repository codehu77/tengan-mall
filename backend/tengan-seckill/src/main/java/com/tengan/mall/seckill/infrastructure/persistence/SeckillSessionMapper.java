package com.tengan.mall.seckill.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SeckillSessionMapper extends BaseMapper<SeckillSessionPO> {

    @Select("SELECT * FROM seckill_session ORDER BY sort_order ASC, time_of_day ASC")
    List<SeckillSessionPO> findAllOrdered();
}
