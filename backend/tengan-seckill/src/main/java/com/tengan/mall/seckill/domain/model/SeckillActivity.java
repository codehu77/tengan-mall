package com.tengan.mall.seckill.domain.model;

import com.tengan.mall.seckill.domain.exception.ActivityStatusTransitionNotAllowedException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * 聚合根：一場搶購活動（限時搶購或首發，見 {@link ActivityType}）。時間窗（startTime/endTime）
 * 兩種類型都必填——首發也需要結束時間（例如開賣後 30 分鐘），用來消化第一波流量，不是無限期開賣
 * （見 Phase 9 規劃 Context 第 2 點）。
 *
 * <p>FLASH_SALE 走固定「場次」（{@link SeckillSession}）：sessionId/activityDate 記錄這組
 * startTime/endTime 是由哪個場次範本+哪一天算出來的，供公開展示端點查「今天還有哪些場次」用；
 * LAUNCH 不使用場次，sessionId/activityDate 恆為 null，startTime/endTime 由後台自由指定
 * （見場次機制規劃文件）。startTime/endTime 仍然實際存欄位、不是每次現算，warmup/settlement/
 * 三道防線等既有邏輯完全不用感知場次的存在。</p>
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

    /** LAUNCH 專用：自由指定起訖時間，不掛場次。 */
    public static SeckillActivity createLaunch(Instant startTime, Instant endTime) {
        if (startTime == null || endTime == null) {
            throw new IllegalArgumentException("startTime/endTime 不可為 null");
        }
        if (!endTime.isAfter(startTime)) {
            throw new IllegalArgumentException("endTime 必須晚於 startTime");
        }
        return new SeckillActivity(null, ActivityType.LAUNCH, startTime, endTime, null, null, ActivityStatus.DRAFT,
                Instant.now());
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
