package com.tengan.mall.wallet.infrastructure.persistence;

/** GROUP BY type, status 查詢的原始列，MyBatis 靠 map-underscore-to-camel-case 自動對應欄位。 */
public class TypeStatusCountRow {

    private Integer type;
    private Integer status;
    private Long cnt;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Long getCnt() {
        return cnt;
    }

    public void setCnt(Long cnt) {
        this.cnt = cnt;
    }
}
