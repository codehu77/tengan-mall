package com.tengan.mall.payment.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface PaymentMethodConfigMapper extends BaseMapper<PaymentMethodConfigPO> {

    @Update("UPDATE payment_method_config SET enabled = #{enabled} WHERE method = #{method}")
    int updateEnabled(@Param("method") String method, @Param("enabled") boolean enabled);
}
