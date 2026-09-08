package com.tengan.mall.devtools.service;

import java.io.IOException;
import java.net.URI;
import java.net.URLConnection;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import org.springframework.stereotype.Component;

/** 下載 MOMO（或其他來源）圖片的原始 bytes，準備重新上傳到自家 tengan-media——不能直接把 MOMO
 * 的圖床網址存進自家資料庫，那個網址不受我們控制，MOMO 隨時可能撤圖或擋熱連結。 */
@Component
public class ImageDownloader {

    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

    public record DownloadedImage(byte[] content, String filename, String contentType) {
    }

    public DownloadedImage download(String imageUrl) {
        try {
            HttpRequest request = HttpRequest.newBuilder(URI.create(imageUrl))
                    .timeout(Duration.ofSeconds(20))
                    .header("User-Agent",
                            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0 Safari/537.36")
                    .GET()
                    .build();
            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());
            if (response.statusCode() / 100 != 2) {
                throw new IllegalStateException("下載圖片失敗（HTTP " + response.statusCode() + "）：" + imageUrl);
            }
            byte[] content = response.body();
            String contentType = response.headers().firstValue("Content-Type").orElse(guessContentType(imageUrl));
            return new DownloadedImage(content, filenameOf(imageUrl), contentType);
        } catch (IOException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("下載圖片失敗：" + imageUrl, e);
        }
    }

    private String guessContentType(String url) {
        String guessed = URLConnection.guessContentTypeFromName(url);
        return guessed != null ? guessed : "image/jpeg";
    }

    private String filenameOf(String url) {
        String path = URI.create(url).getPath();
        int slash = path.lastIndexOf('/');
        String name = slash >= 0 ? path.substring(slash + 1) : path;
        return name.isBlank() ? "image.jpg" : name;
    }
}
