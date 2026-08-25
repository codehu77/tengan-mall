package com.tengan.mall.member.infrastructure.persistence;

/** GROUP BY category_id 查詢的原始列，MyBatis 靠 map-underscore-to-camel-case 自動對應欄位。 */
public class CategoryAffinityScoreRow {

    private Long categoryId;
    private Integer score;

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
