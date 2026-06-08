package com.demo.ticketanalyzer.config;

import io.github.bucket4j.*;
import jakarta.servlet.http.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Token-bucket rate limiter for POST /api/clusters/analyze.
 * Each user gets 5 tokens/minute — tokens refill greedily (not in burst).
 *
 * Interview point: token bucket allows controlled bursting.
 * Refill.greedy = tokens trickle back continuously (vs Refill.intervally
 * which resets all tokens at once — the latter is more abusable).
 */
@Component
@Slf4j
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if ("POST".equalsIgnoreCase(request.getMethod())
                && request.getRequestURI().contains("/clusters/analyze")) {

            String userKey = resolveUser();
            Bucket bucket = buckets.computeIfAbsent(userKey, k -> newBucket());

            if (bucket.tryConsume(1)) {
                response.addHeader("X-RateLimit-Remaining",
                        String.valueOf(bucket.getAvailableTokens()));
                return true;
            }

            log.warn("Rate limit exceeded for user: {}", userKey);
            response.setStatus(429);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(
                    "{\"code\":\"RATE_LIMITED\",\"message\":\"Max 5 analysis requests per minute.\"}");
            return false;
        }

        return true;
    }

    private String resolveUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonymous";
    }

    private Bucket newBucket() {
        return Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1))))
                .build();
    }
}
