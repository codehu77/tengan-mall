package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SkuLaunchConfigMapper extends BaseMapper<SkuLaunchConfigPO> {

    @Insert("INSERT INTO sku_launch_config (sku_id, sale_start_time, purchase_limit_per_user) "
            + "VALUES (#{skuId}, #{saleStartTime}, #{purchaseLimitPerUser}) "
            + "ON DUPLICATE KEY UPDATE sale_start_time = VALUES(sale_start_time), "
            + "purchase_limit_per_user = VALUES(purchase_limit_per_user)")
    int upsert(@Param("skuId") Long skuId, @Param("saleStartTime") LocalDateTime saleStartTime,
            @Param("purchaseLimitPerUser") Integer purchaseLimitPerUser);
}
