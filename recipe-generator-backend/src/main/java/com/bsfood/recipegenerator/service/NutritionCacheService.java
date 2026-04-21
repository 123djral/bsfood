package com.bsfood.recipegenerator.service;

import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 营养分析缓存服务 - 基于食材组合缓存营养分析结果
 */
@Service
public class NutritionCacheService {

    /**
     * 缓存条目
     */
    public static class CacheEntry {
        public final Object data;
        public final long createTime;
        public final long expireTime;

        public CacheEntry(Object data, long ttlMillis) {
            this.data = data;
            this.createTime = System.currentTimeMillis();
            this.expireTime = this.createTime + ttlMillis;
        }

        public boolean isExpired() {
            return System.currentTimeMillis() > expireTime;
        }
    }

    // 缓存存储，key为食材组合的hash
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    // 缓存有效期：1小时
    private static final long CACHE_TTL = 60 * 60 * 1000L;

    /**
     * 生成食材组合的缓存key
     * @param foodIds 食材ID列表（排序后）
     * @return 缓存key
     */
    public String generateKey(Long[] foodIds) {
        // 排序确保相同食材组合产生相同的key
        Long[] sorted = foodIds.clone();
        java.util.Arrays.sort(sorted);
        StringBuilder sb = new StringBuilder();
        for (Long id : sorted) {
            sb.append(id).append(",");
        }
        return hashKey(sb.toString());
    }

    /**
     * 获取缓存的营养分析结果
     * @param key 缓存key
     * @return 缓存的结果，如果不存在或已过期返回null
     */
    public Object get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) {
            return null;
        }
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.data;
    }

    /**
     * 缓存营养分析结果
     * @param key 缓存key
     * @param data 营养分析结果数据
     */
    public void put(String key, Object data) {
        cache.put(key, new CacheEntry(data, CACHE_TTL));
    }

    /**
     * 清除指定缓存
     * @param key 缓存key
     */
    public void evict(String key) {
        cache.remove(key);
    }

    /**
     * 清除所有缓存
     */
    public void clear() {
        cache.clear();
    }

    /**
     * MD5哈希
     */
    private String hashKey(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            // MD5总是可用，如果失败使用原始字符串
            return input;
        }
    }
}
