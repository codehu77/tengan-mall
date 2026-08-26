package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("sku_launch_config")
public class SkuLaunchConfigPO {

    @TableId
    private Long skuId;
    private LocalDateTime saleStartTime;
    private Integer purchaseLimitPerUser;
    private LocalDateTime updatedAt;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public LocalDateTime getSaleStartTime() {
        return saleStartTime;
    }

    public void setSaleStartTime(LocalDateTime saleStartTime) {
        this.saleStartTime = saleStartTime;
    }

    public Integer getPurchaseLimitPerUser() {
        return purchaseLimitPerUser;
    }

    public void setPurchaseLimitPerUser(Integer purchaseLimitPerUser) {
        this.purchaseLimitPerUser = purchaseLimitPerUser;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
