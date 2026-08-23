package com.tengan.mall.media.infrastructure.storage;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.SetBucketPolicyArgs;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 啟動時確保 bucket 存在（比照 RegisteredClientSeeder 的「冪等 seed」寫法），並設定匿名唯讀 policy——
 * 這個 demo 專案圖片上傳完直接要能給瀏覽器 &lt;img&gt; 用，不另外寫一支代理 GET 端點，所以 bucket
 * 一開始就要是公開唯讀，不用等到真的有請求進來才發現讀不到圖。
 */
@Component
public class MinioBucketInitializer implements ApplicationRunner {

    private final MinioClient minioClient;
    private final MinioProperties properties;

    public MinioBucketInitializer(MinioClient minioClient, MinioProperties properties) {
        this.minioClient = minioClient;
        this.properties = properties;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        String bucket = properties.getBucket();
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
        }
        minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(publicReadPolicy(bucket))
                .build());
    }

    private String publicReadPolicy(String bucket) {
        return """
                {
                  "Version": "2012-10-17",
                  "Statement": [
                    {
                      "Effect": "Allow",
                      "Principal": {"AWS": ["*"]},
                      "Action": ["s3:GetObject"],
                      "Resource": ["arn:aws:s3:::%s/*"]
                    }
                  ]
                }
                """.formatted(bucket);
    }
}
