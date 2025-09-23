package com.dayaeyak.performance.config;

import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.HashMap;

@Configuration
@EnableCaching
@Profile("!test")
public class RedisCacheConfig {

    private final RedissonClient redissonClient;

    public RedisCacheConfig(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Bean
    public RedissonSpringCacheManager cacheManager() {
        return new RedissonSpringCacheManager(redissonClient, new HashMap<>());
    }

}