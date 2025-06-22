package org.example.mateproduction.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    public boolean isViewCounted(UUID adId, String ip, String userAgent) {
        String key = buildViewKey(adId, ip, userAgent);
        Boolean exists = redisTemplate.hasKey(key);
        if (Boolean.TRUE.equals(exists)) return true;

        redisTemplate.opsForValue().set(key, "1", Duration.ofMinutes(10));
        return false;
    }

    public void incrementViews(UUID adId) {
        String key = "views:" + adId;
        redisTemplate.opsForValue().increment(key);
    }

    private String buildViewKey(UUID adId, String ip, String userAgent) {
        return "viewed:" + adId + ":" + ip + ":" + userAgent.hashCode();
    }
}
