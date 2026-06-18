package com.safetycampus.common.utils;

import com.safetycampus.common.enums.AlarmDict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

@Component
public class AlarmNoGenerator {

    private final RedisTemplate<String, Object> redisTemplate;

    public AlarmNoGenerator(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generate() {
        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String key = AlarmDict.REDIS_KEY_ALARM_NO_SEQUENCE + dateStr;

        Long sequence = redisTemplate.opsForValue().increment(key);
        if (sequence == null || sequence == 1) {
            redisTemplate.expire(key, 2, TimeUnit.DAYS);
        }

        return AlarmDict.ALARM_NO_PREFIX + dateStr + String.format("%06d", sequence);
    }
}
