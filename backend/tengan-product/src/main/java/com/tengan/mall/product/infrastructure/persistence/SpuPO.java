package com.tengan.mall.product.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tengan.mall.product.domain.model.SpuStatus;
import java.time.LocalDateTime;

@TableName("spu")
public class SpuPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long categoryId;
    private Long brandId;
    private String name;
    private String description;
    private String mainImage;
    private SpuStatus status;
    private LocalDateTime saleStartTime;
    private Boolean trafficGateEnabled;
    private LocalDateTime gateCloseTime;
    private Boolean showOnLaunchTeaser;
    private LocalDateTime teaserRemoveAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Long getBrandId() {
        return brandId;
    }

    public void setBrandId(Long brandId) {
        this.brandId = brandId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMainImage() {
        return mainImage;
    }

    public void setMainImage(String mainImage) {
        this.mainImage = mainImage;
    }

    public SpuStatus getStatus() {
        return status;
    }

    public void setStatus(SpuStatus status) {
        this.status = status;
    }

    public LocalDateTime getSaleStartTime() {
        return saleStartTime;
    }

    public void setSaleStartTime(LocalDateTime saleStartTime) {
        this.saleStartTime = saleStartTime;
    }

    public Boolean getTrafficGateEnabled() {
        return trafficGateEnabled;
    }

    public void setTrafficGateEnabled(Boolean trafficGateEnabled) {
        this.trafficGateEnabled = trafficGateEnabled;
    }

    public LocalDateTime getGateCloseTime() {
        return gateCloseTime;
    }

    public void setGateCloseTime(LocalDateTime gateCloseTime) {
        this.gateCloseTime = gateCloseTime;
    }

    public Boolean getShowOnLaunchTeaser() {
        return showOnLaunchTeaser;
    }

    public void setShowOnLaunchTeaser(Boolean showOnLaunchTeaser) {
        this.showOnLaunchTeaser = showOnLaunchTeaser;
    }

    public LocalDateTime getTeaserRemoveAt() {
        return teaserRemoveAt;
    }

    public void setTeaserRemoveAt(LocalDateTime teaserRemoveAt) {
        this.teaserRemoveAt = teaserRemoveAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
