package com.tengan.mall.seckill.domain.model;

import com.tengan.mall.seckill.domain.exception.ActivityStatusTransitionNotAllowedException;
import java.time.Instant;

/**
 * 聚合根：一場搶購活動（限時搶購或首發，見 {@link ActivityType}）。時間窗（startTime/endTime）
 * 兩種類型都必填——首發也需要結束時間（例如開賣後 30 分鐘），用來消化第一波流量，不是無限期開賣
 * （見 Phase 9 規劃 Context 第 2 點）。
 */
public class SeckillActivity {

    private Long id;
    private final ActivityType activityType;
    private final Instant startTime;
    private final Instant endTime;
    private ActivityStatus status;
    private final Instant createdAt;

    private SeckillActivity(Long id, ActivityType activityType, Instant startTime, Instant endTime,
            ActivityStatus status, Instant createdAt) {
        this.id = id;
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static SeckillActivity create(ActivityType activityType, Instant startTime, Instant endTime) {
        if (activityType == null) {
            throw new IllegalArgumentException("activityType 不可為 null");
        }
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("startTime/endTime 不可為 null，兩種活動類型都必填結束時間");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime 必須晚於 startTime");
        }
        return new SeckillActivity(null, activityType, startTime, endTime, ActivityStatus.DRAFT, Instant.now());
    }

    public static SeckillActivity reconstitute(Long id, ActivityType activityType, Instant startTime,
            Instant endTime, ActivityStatus status, Instant createdAt) {
        return new SeckillActivity(id, activityType, startTime, endTime, status, createdAt);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("SeckillActivity 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    /** 後台確認可以排入預熱排程。 */
    public void publish() {
        requireStatus(ActivityStatus.DRAFT, ActivityStatus.PUBLISHED);
        this.status = ActivityStatus.PUBLISHED;
    }

    /** 預熱排程處理過（Redis 配額已就緒）才轉為 ACTIVE，避免重複預熱。 */
    public void activate() {
        requireStatus(ActivityStatus.PUBLISHED, ActivityStatus.ACTIVE);
        this.status = ActivityStatus.ACTIVE;
    }

    /** 結算完成（真實庫存已同步）才轉為 SETTLED，之後不會再被結算排程撈到。 */
    public void settle() {
        requireStatus(ActivityStatus.ACTIVE, ActivityStatus.SETTLED);
        this.status = ActivityStatus.SETTLED;
    }

    private void requireStatus(ActivityStatus expected, ActivityStatus target) {
        if (status != expected) {
            throw new ActivityStatusTransitionNotAllowedException(id, status, target);
        }
    }

    public Long getId() {
        return id;
    }

    public ActivityType getActivityType() {
        return activityType;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public Instant getEndTime() {
        return endTime;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
