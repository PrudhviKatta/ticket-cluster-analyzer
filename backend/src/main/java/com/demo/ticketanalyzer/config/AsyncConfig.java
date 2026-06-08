package com.demo.ticketanalyzer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * Dedicated thread pool for Groq API calls.
 * Interview point: without a bounded pool, every analyze request creates
 * an unbounded thread — under load this exhausts memory.
 * Core=2 means at most 2 concurrent Groq calls; queue buffers the rest.
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Value("${app.async.core-pool-size:2}")
    private int corePoolSize;

    @Value("${app.async.max-pool-size:5}")
    private int maxPoolSize;

    @Value("${app.async.queue-capacity:10}")
    private int queueCapacity;

    @Bean(name = "groqTaskExecutor")
    public Executor groqTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setThreadNamePrefix("groq-worker-");
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.initialize();
        return executor;
    }
}
