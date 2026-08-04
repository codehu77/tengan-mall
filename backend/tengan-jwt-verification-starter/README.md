# tengan-jwt-verification-starter

Cross-cutting 技術碼的共用模組，是 `docs/ddd-standards.md` 第九節「不建共用 `common` module」規則下**唯一允許的例外**。每個微服務都需要驗證 JWT 簽章，把這段邏輯寫一次、包成 Spring Boot starter，其他服務只要加依賴 + 寫設定就能用，不用各自重寫。

## 這是什麼：Spring Boot Starter

一般套件要手動 `new` 出來用；**starter** 指的是「只要加進 `pom.xml` 依賴，Spring Boot 啟動時就自動幫你組裝好對應元件（Bean），不需要手動註冊」的套件，靠的是 Spring Boot 的 **auto-configuration** 機制：

1. Spring Boot 啟動時掃描 classpath 上所有 jar，尋找 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 這份清單
2. 清單裡列的類別（本模組是 [`JwtVerificationAutoConfiguration`](src/main/java/com/tengan/mall/jwt/JwtVerificationAutoConfiguration.java)）會被自動載入
3. 該類別裡用 `@Bean` 宣告的元件，只要對應的 `application.yml` 屬性存在（`@ConditionalOnProperty`），就會被自動建立，可以直接注入使用

## 提供什麼

| Bean / 類別 | 生效條件 | 用途 |
|---|---|---|
| `JwtDecoder userJwtDecoder` | 設定了 `tengan.jwt.user.jwk-set-uri` | 驗證 customer 主體的原始 JWT（`/api/customer/**` 用，由 `tengan-auth` 簽發） |
| `JwtDecoder adminJwtDecoder` | 設定了 `tengan.jwt.admin.jwk-set-uri` | 驗證 admin 主體的原始 JWT（`/api/admin/**` 用，由 `tengan-admin` 自己簽發——admin_user 帳密不在 `tengan-auth` 的 `account` 表，是獨立主體，用自己的簽章金鑰）。目前只有 `tengan-gateway` 會設定這組，因為它要同時驗兩種主體的 token；`tengan-admin` 驗自己簽的 token 時沿用 `user` 這組即可，不用另外設定 `admin` |
| `JwtDecoder serviceJwtDecoder` | 設定了 `tengan.jwt.service.jwk-set-uri` | 驗證 Service JWT（`/internal/**` 用，由 `tengan-auth` 掛載的 Spring Authorization Server 核發） |
| `IdentityAssertionVerifier userIdentityAssertionVerifier` | 設定了 `tengan.jwt.user.jwk-set-uri` | 解析並驗證 customer 主體轉發的 `X-Identity-Assertion` header，借用跟一般使用者 JWT 相同的 `userJwtDecoder` |
| `IdentityAssertionVerifier adminIdentityAssertionVerifier` | 設定了 `tengan.jwt.admin.jwk-set-uri` | 解析並驗證 `tengan-admin` 後台 BFF 轉發的 `X-Identity-Assertion` header（呼叫 `/internal/**` 時附帶，讓下游服務知道是哪個管理員做的），借用跟 admin JWT 相同的 `adminJwtDecoder` |

**三顆 `JwtDecoder` 各自獨立**，對應 `docs/JWT設計.md` 的設計：customer/admin/service 是三種不同主體，各自一把簽章金鑰，不能共用同一個解碼器，否則等於讓一種憑證可以冒充另一種身份。各自用 `@ConditionalOnProperty` 控制是否產生，用不到的主體類型不會被迫多出用不到的 Bean。

## 怎麼用

**1. 加依賴**（`pom.xml`）：

```xml
<dependency>
  <groupId>com.tengan.mall</groupId>
  <artifactId>tengan-jwt-verification-starter</artifactId>
</dependency>
```

**2. 加設定**（`application.yml`，依服務實際需要的部分設定，不用三組都填）：

```yaml
tengan:
  jwt:
    user:
      jwk-set-uri: http://tengan-auth/oauth2/jwks
    admin:
      jwk-set-uri: http://tengan-admin/oauth2/jwks   # 只有需要同時驗兩種主體的服務（目前是 Gateway）才設定
    service:
      jwk-set-uri: http://tengan-auth/oauth2/jwks
```

**3. 直接注入使用**，不需要自己寫任何 `NimbusJwtDecoder` 相關程式碼：

```java
public class SomeSecurityConfig {

    private final JwtDecoder userJwtDecoder; // 建構子注入，依 ddd-standards.md 慣例

    public SomeSecurityConfig(JwtDecoder userJwtDecoder) {
        this.userJwtDecoder = userJwtDecoder;
    }
}
```

如果同一個服務兩顆都有設定（既要驗使用者 JWT、又要驗 Service JWT），依賴注入時用 `@Qualifier("userJwtDecoder")` / `@Qualifier("serviceJwtDecoder")` 指定要哪一顆，避免 Spring 因為有兩個 `JwtDecoder` 型別的 Bean 而報錯。

## 檔案結構對照

```
src/main/java/com/tengan/mall/jwt/
├── TenganJwtProperties.java           # 對應 application.yml 的 tengan.jwt.* 設定（型別安全，IDE 有自動完成）
├── JwtVerificationAutoConfiguration.java  # 真正的自動配置類別，宣告上面表格的四個 Bean
└── IdentityAssertionVerifier.java     # 純 Java 類別，沒有 @Component，由上面的 @Bean 方法手動建立

src/main/resources/META-INF/spring/
└── org.springframework.boot.autoconfigure.AutoConfiguration.imports
    # 不是程式碼，是一份「清單」文字檔，告訴 Spring Boot 這個 jar 裡有自動配置類別要載入。
    # 放在 resources 而不是 java：resources 底下的內容不會被編譯，是原封不動複製進最終 jar 的
    # 附帶檔案（設定檔、清單、靜態資源都放這裡），跟要編譯的 .java 原始碼是不同層次的東西。
```

## 驗證方式

```bash
cd backend
mvn clean compile   # 需要 JDK 21（見 pom.xml 的 maven.compiler.release）
```
