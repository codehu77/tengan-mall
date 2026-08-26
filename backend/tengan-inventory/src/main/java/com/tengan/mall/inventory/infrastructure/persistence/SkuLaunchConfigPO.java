package com.tengan.mall.inventory.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDateTime;

@TableName("sku_launch_config")
public class SkuLaunchConfigPO {

    @TableId
    private Long skuId;
    private LocalDateTime saleStartTime;
    private boolean trafficGateEnabled;
    private LocalDateTime gateCloseTime;
    private Integer purchaseLimitPerUser;
    private Integer gateProtectedStock;
    private LocalDateTime gateWarmedAt;
    private LocalDateTime gateSettledAt;
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

    public boolean isTrafficGateEnabled() {
        return trafficGateEnabled;
    }

    public void setTrafficGateEnabled(boolean trafficGateEnabled) {
        this.trafficGateEnabled = trafficGateEnabled;
    }

    public LocalDateTime getGateCloseTime() {
        return gateCloseTime;
    }

    public void setGateCloseTime(LocalDateTime gateCloseTime) {
        this.gateCloseTime = gateCloseTime;
    }

    public Integer getPurchaseLimitPerUser() {
        return purchaseLimitPerUser;
    }

    public void setPurchaseLimitPerUser(Integer purchaseLimitPerUser) {
        this.purchaseLimitPerUser = purchaseLimitPerUser;
    }

    public Integer getGateProtectedStock() {
        return gateProtectedStock;
    }

    public void setGateProtectedStock(Integer gateProtectedStock) {
        this.gateProtectedStock = gateProtectedStock;
    }

    public LocalDateTime getGateWarmedAt() {
        return gateWarmedAt;
    }

    public void setGateWarmedAt(LocalDateTime gateWarmedAt) {
        this.gateWarmedAt = gateWarmedAt;
    }

    public LocalDateTime getGateSettledAt() {
        return gateSettledAt;
    }

    public void setGateSettledAt(LocalDateTime gateSettledAt) {
        this.gateSettledAt = gateSettledAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
