package com.example.mainserver.cache;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class CacheConfig {

    private static final String SERVER_NAMESPACE = "mainserver:";

    private RedisCacheConfiguration redisCacheConfiguration(){
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .entryTtl(Duration.ofSeconds(90))
                .computePrefixWith(cacheName -> SERVER_NAMESPACE + cacheName + "::");
    }

    private Map<String, RedisCacheConfiguration> redisCacheConfigurationsMap(){
        Map<String, RedisCacheConfiguration> redisCacheConfigurationsMap = new HashMap<>();
        RedisCacheConfiguration config = redisCacheConfiguration();
        redisCacheConfigurationsMap.put("cars:Filter", config.entryTtl(Duration.ofSeconds(60)));
        redisCacheConfigurationsMap.put("cars:Detail", config.entryTtl(Duration.ofSeconds(90)));
        redisCacheConfigurationsMap.put("cars:Summary", config.entryTtl(Duration.ofSeconds(30)));
        return redisCacheConfigurationsMap;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory)
                .cacheDefaults(redisCacheConfiguration())
                .withInitialCacheConfigurations(redisCacheConfigurationsMap())
                .allowCreateOnMissingCache(false)
                .build();
    }


}
