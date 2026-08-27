package com.tengan.mall.seckill.domain.model;

import com.tengan.mall.seckill.domain.exception.ActivityStatusTransitionNotAllowedException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 聚合根：一場限時搶購活動（見 {@link ActivityType}）。時間窗（startTime/endTime）由固定「場次」
 * （{@link SeckillSession}）算出：sessionId/activityDate 記錄這組 startTime/endTime 是由哪個
 * 場次範本+哪一天算出來的，供公開展示端點查「今天還有哪些場次」用。
 *
 * <p>2026-08-27：原本還有一個共用同一套骨架的 LAUNCH（首發/流量閘門）類型，已經被獨立的
 * 「即將開賣」+「防超賣保護」功能（tengan-product/tengan-inventory）取代並移除，{@link ActivityType}
 * 目前只剩 FLASH_SALE 一個值。</p>
 */
public class SeckillActivity {

    private Long id;
    private final ActivityType activityType;
    private final Instant startTime;
    private final Instant endTime;
    private final Long sessionId;
    private final LocalDate activityDate;
    private ActivityStatus status;
    private final Instant createdAt;

    private SeckillActivity(Long id, ActivityType activityType, Instant startTime, Instant endTime, Long sessionId,
            LocalDate activityDate, ActivityStatus status, Instant createdAt) {
        this.id = id;
        this.activityType = activityType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionId = sessionId;
        this.activityDate = activityDate;
        this.status = status;
        this.createdAt = createdAt;
    }

    /** FLASH_SALE 專用：時間窗由場次範本的 timeOfDay/durationMinutes + 指定日期算出（伺服器所在時區）。 */
    public static SeckillActivity createFlashSale(SeckillSession session, LocalDate activityDate) {
        if (session == null) {
            throw new IllegalArgumentException("session 不可為 null");
        }
        if (activityDate == null) {
            throw new IllegalArgumentException("activityDate 不可為 null");
        }
        Instant startTime = activityDate.atTime(session.getTimeOfDay()).atZone(ZoneId.systemDefault()).toInstant();
        Instant endTime = startTime.plus(Duration.ofMinutes(session.getDurationMinutes()));
        return new SeckillActivity(null, ActivityType.FLASH_SALE, startTime, endTime, session.getId(), activityDate,
                ActivityStatus.DRAFT, Instant.now());
    }

    public static SeckillActivity reconstitute(Long id, ActivityType activityType, Instant startTime,
            Instant endTime, Long sessionId, LocalDate activityDate, ActivityStatus status, Instant createdAt) {
        return new SeckillActivity(id, activityType, startTime, endTime, sessionId, activityDate, status, createdAt);
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

    public Long getSessionId() {
        return sessionId;
    }

    public LocalDate getActivityDate() {
        return activityDate;
    }

    public ActivityStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
