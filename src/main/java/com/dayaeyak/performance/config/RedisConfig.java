package com.dayaeyak.performance.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String redisHost;

    @Value("${spring.data.redis.port}")
    private int redisPort;

    @Value("${spring.data.redis.password:}")
    private String redisPassword;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();

        // Redis 서버 설정
        String address = String.format("redis://%s:%d", redisHost, redisPort);

        if (redisPassword != null && !redisPassword.isEmpty()) {
            config.useSingleServer()
                    .setAddress(address)
                    .setPassword(redisPassword)
                    .setConnectionMinimumIdleSize(10)
                    .setConnectionPoolSize(20)
                    .setDatabase(0);
        } else {
            config.useSingleServer()
                    .setAddress(address)
                    .setConnectionMinimumIdleSize(10)
                    .setConnectionPoolSize(20)
                    .setDatabase(0);
        }

        return Redisson.create(config);
    }
}
