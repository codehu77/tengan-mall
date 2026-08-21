# tengan-snowflake-starter

Cross-cutting 技術碼的共用模組，是 `docs/ddd-standards.md` 第九節「不建共用 `common` module」規則下的允許例外之一（跟 `tengan-jwt-verification-starter` 屬於同一類）。提供一顆保證唯一的分散式 ID 產生器（Snowflake），取代原本 `tengan-order` 用 timestamp+random 做的機率性防碰撞——秒殺場景下同一秒可能有上百筆訂單同時產生 orderSn，機率性防碰撞的碰撞率不可忽略，需要真正保證唯一的演算法。

## 這是什麼：Spring Boot Starter

只要加進 `pom.xml` 依賴，Spring Boot 啟動時就會透過 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 自動載入 `SnowflakeAutoConfiguration`，把 `SnowflakeIdGenerator` 組裝成 Bean，不需要手動註冊（跟 `tengan-jwt-verification-starter` 是同一套機制，細節見那個模組的 README）。

## 提供什麼

| Bean / 類別 | 用途 |
|---|---|
| `SnowflakeIdGenerator` | 標準 Twitter Snowflake：`nextId()` 回傳保證唯一的 `long`（41 bit 時間戳 + 5 bit datacenterId + 5 bit workerId + 12 bit 序列號），呼叫端 `Long.toString()` 後可以直接存進既有的 `VARCHAR(64)` order_sn 欄位，不用動任何下游表結構 |

## 怎麼用

**1. 加依賴**（`pom.xml`）：

```xml
<dependency>
  <groupId>com.tengan.mall</groupId>
  <artifactId>tengan-snowflake-starter</artifactId>
</dependency>
```

**2. 加設定**（`application.yml`，各服務要給不重複的 `worker-id`）：

```yaml
tengan:
  snowflake:
    worker-id: 1       # 0~31，同一時間可能各自產生 ID 的服務要給不重複的值
    datacenter-id: 0    # 0~31，本專案只有單一資料中心，固定給 0
```

目前只有 `tengan-order`（worker-id=1）跟 `tengan-seckill`（worker-id=2）需要設定——兩者都會產生 orderSn，各給不重複的 worker-id 才能避免同一毫秒生出相同的 ID。沒設定的服務走預設值 0，就算誤帶了這個依賴、沒有實際注入使用也不會出錯。

**3. 直接注入使用**：

```java
public class SomeService {

    private final SnowflakeIdGenerator snowflakeIdGenerator; // 建構子注入，依 ddd-standards.md 慣例

    public SomeService(SnowflakeIdGenerator snowflakeIdGenerator) {
        this.snowflakeIdGenerator = snowflakeIdGenerator;
    }

    public String generateOrderSn() {
        return Long.toString(snowflakeIdGenerator.nextId());
    }
}
```

## 驗證方式

```bash
cd backend
mvn clean compile   # 需要 JDK 21（見 pom.xml 的 maven.compiler.release）
```
