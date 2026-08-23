package com.tengan.mall.media.infrastructure.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;

/** 對應 Nacos tengan-mall-media.yaml 的 tengan.media.minio.* 設定。 */
@ConfigurationProperties(prefix = "tengan.media.minio")
public class MinioProperties {

    /** 服務端 SDK 用來連線 MinIO 的位址（這台機器開發環境跟瀏覽器存取用同一個 localhost 位址）。 */
    private String endpoint = "http://localhost:9000";
    private String accessKey;
    private String secretKey;
    private String bucket = "tengan-media";

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }
}
