package com.example.dividend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor
@Configuration
public class CacheConfig {

    @Value("${spring.redis.host}") // application.properties 파일에서 spring.redis.host 값을 가져옴
    private String host;

    @Value("${spring.redis.port}") // application.properties 파일에서 spring.redis.port 값을 가져옴
    private int port;

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration conf = RedisCacheConfiguration.defaultCacheConfig()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // Redis에 데이터를 저장할 때, 키와 값을 직렬화하는 설정. 키는 StringRedisSerializer로, 값은 GenericJackson2JsonRedisSerializer로 직렬화.

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(conf)
                .build();
        // RedisCacheManager를 빌드하고 반환. 미리 생성된 Redis 연결 팩토리를 사용하여 RedisCacheManager를 생성하고 설정(conf)을 기본 값으로 지정하여 반환.
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration();
        conf.setHostName(this.host); // Redis 서버 호스트 이름 설정
        conf.setPort(this.port); // Redis 서버 포트 번호 설정
        return new LettuceConnectionFactory(conf);
        // LettuceConnectionFactory를 사용하여 RedisConnectionFactory를 생성하고 반환.
        // LettuceConnectionFactory는 Redis 서버와의 연결을 만들기 위해 Lettuce 라이브러리를 사용합니다.
    }
}
