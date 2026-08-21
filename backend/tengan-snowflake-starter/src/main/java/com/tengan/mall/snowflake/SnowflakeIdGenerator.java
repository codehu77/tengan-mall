package com.tengan.mall.snowflake;

/**
 * 標準 Twitter Snowflake：64 bit long 拆成「41 bit 時間戳（ms，相對 {@link #EPOCH}）+ 5 bit
 * datacenterId + 5 bit workerId + 12 bit 序列號」。天生保證唯一（不像 tengan-order 原本的
 * timestamp+random 只是機率性防碰撞），單一 worker 每毫秒最多可產生 4096 個不重複 ID。
 *
 * <p>{@code datacenterId}/{@code workerId} 由呼叫端在建構時指定（見 {@link TenganSnowflakeProperties}），
 * 兩個會同時產生 ID 的服務（目前是 tengan-order、tengan-seckill）要各自給不重複的 workerId，
 * 避免同一毫秒生出相同的 ID。</p>
 */
public class SnowflakeIdGenerator {

    /** 2025-01-01T00:00:00Z，自訂起始時間，縮短時間戳需要的位元數，不是必須對齊任何業務事件。 */
    private static final long EPOCH = 1735689600000L;

    private static final long DATACENTER_ID_BITS = 5L;
    private static final long WORKER_ID_BITS = 5L;
    private static final long SEQUENCE_BITS = 12L;

    private static final long MAX_DATACENTER_ID = ~(-1L << DATACENTER_ID_BITS);
    private static final long MAX_WORKER_ID = ~(-1L << WORKER_ID_BITS);
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long DATACENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATACENTER_ID_BITS;

    private final long datacenterId;
    private final long workerId;

    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long datacenterId, long workerId) {
        if (datacenterId < 0 || datacenterId > MAX_DATACENTER_ID) {
            throw new IllegalArgumentException("datacenterId 必須介於 0~" + MAX_DATACENTER_ID + "，實際=" + datacenterId);
        }
        if (workerId < 0 || workerId > MAX_WORKER_ID) {
            throw new IllegalArgumentException("workerId 必須介於 0~" + MAX_WORKER_ID + "，實際=" + workerId);
        }
        this.datacenterId = datacenterId;
        this.workerId = workerId;
    }

    /**
     * 執行緒安全：{@code synchronized} 保護 {@link #sequence}/{@link #lastTimestamp} 這兩個
     * 可變狀態。單一 JVM 內序列號用 synchronized 而不是 CAS，是因為每次呼叫本來就要做「判斷是否
     * 同一毫秒」這種多步驟邏輯，CAS 沒有明顯優勢，synchronized 寫法更直接。
     */
    public synchronized long nextId() {
        long timestamp = currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new IllegalStateException(
                    "偵測到系統時鐘回撥，拒絕產生 ID（lastTimestamp=" + lastTimestamp + ", now=" + timestamp + "）");
        }

        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & SEQUENCE_MASK;
            if (sequence == 0) {
                // 同一毫秒內的 4096 個序列號用完了，自旋等到下一毫秒才繼續
                timestamp = waitNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }
        lastTimestamp = timestamp;

        return ((timestamp - EPOCH) << TIMESTAMP_SHIFT)
                | (datacenterId << DATACENTER_ID_SHIFT)
                | (workerId << WORKER_ID_SHIFT)
                | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = currentTimeMillis();
        }
        return timestamp;
    }

    private long currentTimeMillis() {
        return System.currentTimeMillis();
    }
}
