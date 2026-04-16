package com.bsfood.recipegenerator.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 图片下载器 - 将网络图片下载到本地保存
 */
@Component
public class ImageDownloader {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(30))
            .build();

    // 本地图片保存路径
    private static final String LOCAL_PICTURE_PATH = "C:/develop/codeBase/bsfood1/picture";

    // 缓存已处理的图片URL，避免重复下载
    private final ConcurrentHashMap<String, String> imageUrlCache = new ConcurrentHashMap<>();

    /**
     * 下载图片并保存到本地
     * @param imageUrl 网络图片URL
     * @param fileName 保存的文件名（不含扩展名）
     * @return 本地保存后的文件路径
     */
    public String downloadAndSaveImage(String imageUrl, String fileName) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            return null;
        }

        try {
            // 确保目录存在
            Path dirPath = Paths.get(LOCAL_PICTURE_PATH);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            // 清理文件名，只保留合法字符
            String cleanFileName = cleanFileName(fileName);
            String savedPath = LOCAL_PICTURE_PATH + "/" + cleanFileName + ".jpg";
            Path targetPath = Paths.get(savedPath);

            // 如果文件已存在，直接返回本地路径
            if (Files.exists(targetPath)) {
                System.out.println(">>> 图片已存在，直接使用本地文件: " + savedPath);
                return savedPath;
            }

            // 下载图片
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(imageUrl))
                    .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(Duration.ofSeconds(60))
                    .GET()
                    .build();

            HttpResponse<byte[]> response = httpClient.send(request, HttpResponse.BodyHandlers.ofByteArray());

            if (response.statusCode() == 200) {
                byte[] imageBytes = response.body();
                Files.write(targetPath, imageBytes);
                System.out.println(">>> 图片已保存到: " + savedPath + " (大小: " + imageBytes.length + " bytes)");
                return savedPath;
            } else {
                System.out.println(">>> 图片下载失败，状态码: " + response.statusCode());
                return null;
            }
        } catch (IOException | InterruptedException e) {
            System.out.println(">>> 图片下载异常: " + e.getMessage());
            return null;
        }
    }

    /**
     * 获取本地图片路径，如果不存在则返回null
     */
    public String getLocalImagePath(String fileName) {
        String cleanFileName = cleanFileName(fileName);
        String savedPath = LOCAL_PICTURE_PATH + "/" + cleanFileName + ".jpg";
        Path path = Paths.get(savedPath);
        if (Files.exists(path)) {
            return savedPath;
        }
        return null;
    }

    /**
     * 清理文件名，移除非法字符
     */
    private String cleanFileName(String fileName) {
        if (fileName == null) {
            fileName = "unknown";
        }
        // 移除或替换非法字符
        return fileName.replaceAll("[\\\\/:*?\"<>|]", "_")
                       .replaceAll("\\s+", "_")
                       .substring(0, Math.min(fileName.length(), 100));
    }

    /**
     * 获取本地图片的访问URL（相对路径）
     */
    public String getImageUrl(String fileName) {
        String localPath = getLocalImagePath(fileName);
        if (localPath != null) {
            // 返回相对路径供前端访问
            return "/picture/" + cleanFileName(fileName) + ".jpg";
        }
        return null;
    }
}
