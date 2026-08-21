package com.tengan.mall.snowflake;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 對應 application.yml 的 tengan.snowflake.* 設定。兩個值都沒設定時預設 0——只有真的需要
 * SnowflakeIdGenerator 的服務（目前是 tengan-order、tengan-seckill）才需要在 Nacos yaml
 * 各給一組不重複的 worker-id，避免兩邊同毫秒生出相同 ID；其他服務就算誤帶了這個依賴，
 * 沒有實際注入 SnowflakeIdGenerator 使用也不會出錯。
 */
@ConfigurationProperties(prefix = "tengan.snowflake")
public class TenganSnowflakeProperties {

    /** 0~31，同一時間可能各自產生 ID 的服務要給不重複的值。 */
    private long workerId = 0L;

    /** 0~31，這個專案目前只有單一資料中心，固定給 0 即可，保留這個維度是標準 Snowflake 設計的一部分。 */
    private long datacenterId = 0L;

    public long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(long workerId) {
        this.workerId = workerId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public void setDatacenterId(long datacenterId) {
        this.datacenterId = datacenterId;
    }
}
