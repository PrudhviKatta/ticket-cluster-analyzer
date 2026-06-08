package com.demo.ticketanalyzer.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine in-process cache — avoids redundant DB reads for ticket list.
 * Interview point: Caffeine beats Guava cache for Java 8+ (non-blocking,
 * Window TinyLFU eviction — near-optimal hit rate).
 * TTL of 5 min means stale data window is bounded even if eviction misses.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("tickets");
        manager.setCaffeine(
                Caffeine.newBuilder()
                        .expireAfterWrite(5, TimeUnit.MINUTES)
                        .maximumSize(500)
                        .recordStats()  // enables cache hit/miss metrics
        );
        return manager;
    }
}
