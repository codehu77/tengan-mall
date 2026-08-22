package com.tengan.mall.seckill.domain.model;

import java.time.LocalTime;

/**
 * 場次範本：FLASH_SALE 專用，定義每日固定開賣時間點+固定時長（例如「早場」10:00 開賣、賣 2 小時）。
 * 後台建立 FLASH_SALE 活動時選一個場次範本 + 一個日期，由 {@link SeckillActivity#createFlashSale}
 * 算出實際的 startTime/endTime。LAUNCH 活動不使用場次，維持自由起訖時間。
 */
public class SeckillSession {

    private Long id;
    private String name;
    private LocalTime timeOfDay;
    private int durationMinutes;
    private int sortOrder;
    private boolean enabled;

    private SeckillSession(Long id, String name, LocalTime timeOfDay, int durationMinutes, int sortOrder,
            boolean enabled) {
        this.id = id;
        this.name = name;
        this.timeOfDay = timeOfDay;
        this.durationMinutes = durationMinutes;
        this.sortOrder = sortOrder;
        this.enabled = enabled;
    }

    public static SeckillSession create(String name, LocalTime timeOfDay, int durationMinutes, int sortOrder,
            boolean enabled) {
        validate(name, timeOfDay, durationMinutes);
        return new SeckillSession(null, name, timeOfDay, durationMinutes, sortOrder, enabled);
    }

    public static SeckillSession reconstitute(Long id, String name, LocalTime timeOfDay, int durationMinutes,
            int sortOrder, boolean enabled) {
        return new SeckillSession(id, name, timeOfDay, durationMinutes, sortOrder, enabled);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("SeckillSession 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void update(String name, LocalTime timeOfDay, int durationMinutes, int sortOrder, boolean enabled) {
        validate(name, timeOfDay, durationMinutes);
        this.name = name;
        this.timeOfDay = timeOfDay;
        this.durationMinutes = durationMinutes;
        this.sortOrder = sortOrder;
        this.enabled = enabled;
    }

    private static void validate(String name, LocalTime timeOfDay, int durationMinutes) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name 不可為空");
        }
        if (timeOfDay == null) {
            throw new IllegalArgumentException("timeOfDay 不可為 null");
        }
        if (durationMinutes <= 0) {
            throw new IllegalArgumentException("durationMinutes 必須大於 0");
        }
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public LocalTime getTimeOfDay() {
        return timeOfDay;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
