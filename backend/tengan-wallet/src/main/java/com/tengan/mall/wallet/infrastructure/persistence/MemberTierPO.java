package com.tengan.mall.wallet.infrastructure.persistence;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.tengan.mall.wallet.domain.model.MemberTierLevel;
import java.time.LocalDateTime;

@TableName("member_tier")
public class MemberTierPO {

    @TableId(value = "member_id", type = IdType.INPUT)
    private Long memberId;
    private MemberTierLevel tier;
    private String updatedBy;
    private String updatedReason;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getMemberId() {
        return memberId;
    }

    public void setMemberId(Long memberId) {
        this.memberId = memberId;
    }

    public MemberTierLevel getTier() {
        return tier;
    }

    public void setTier(MemberTierLevel tier) {
        this.tier = tier;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public String getUpdatedReason() {
        return updatedReason;
    }

    public void setUpdatedReason(String updatedReason) {
        this.updatedReason = updatedReason;
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
