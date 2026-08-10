package com.tengan.mall.order.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItemPO> {

    @Select("SELECT * FROM order_item WHERE order_id = #{orderId}")
    List<OrderItemPO> findByOrderId(@Param("orderId") Long orderId);
}
