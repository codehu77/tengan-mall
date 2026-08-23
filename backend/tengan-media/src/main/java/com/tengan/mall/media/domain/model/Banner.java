package com.tengan.mall.media.domain.model;

/** 首頁輪播圖。純展示內容，沒有狀態機——enabled 只是單純的顯示開關。 */
public class Banner {

    private Long id;
    private String imageUrl;
    private String linkUrl;
    private String title;
    private int sortOrder;
    private boolean enabled;

    private Banner(Long id, String imageUrl, String linkUrl, String title, int sortOrder, boolean enabled) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.title = title;
        this.sortOrder = sortOrder;
        this.enabled = enabled;
    }

    public static Banner create(String imageUrl, String linkUrl, String title, int sortOrder, boolean enabled) {
        validate(imageUrl);
        return new Banner(null, imageUrl, linkUrl, title, sortOrder, enabled);
    }

    public static Banner reconstitute(Long id, String imageUrl, String linkUrl, String title, int sortOrder,
            boolean enabled) {
        return new Banner(id, imageUrl, linkUrl, title, sortOrder, enabled);
    }

    public void assignId(Long id) {
        if (this.id != null) {
            throw new IllegalStateException("Banner 已經有 id，不可重複指派: " + this.id);
        }
        this.id = id;
    }

    public void update(String imageUrl, String linkUrl, String title, int sortOrder, boolean enabled) {
        validate(imageUrl);
        this.imageUrl = imageUrl;
        this.linkUrl = linkUrl;
        this.title = title;
        this.sortOrder = sortOrder;
        this.enabled = enabled;
    }

    private static void validate(String imageUrl) {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new IllegalArgumentException("imageUrl 不可為空");
        }
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public String getTitle() {
        return title;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
