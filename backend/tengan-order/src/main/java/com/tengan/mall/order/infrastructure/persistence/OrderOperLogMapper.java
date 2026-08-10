package com.tengan.mall.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderOperLogMapper extends BaseMapper<OrderOperLogPO> {
}
