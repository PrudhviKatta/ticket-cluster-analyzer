package com.demo.ticketanalyzer.client;

import com.demo.ticketanalyzer.exception.ServiceUnavailableException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Isolated HTTP client for Groq API with circuit breaker.
 *
 * Interview point: extracting the HTTP call into its own @Component means:
 * 1. @CircuitBreaker AOP proxy works (Spring proxies can only intercept
 *    calls coming from OTHER beans — self-invocation bypasses the proxy).
 * 2. Single Responsibility — GroqClusteringService handles logic,
 *    GroqApiClient handles transport.
 * 3. Easy to mock in tests.
 *
 * Circuit breaker states:
 *   CLOSED  → requests flow normally
 *   OPEN    → requests fail fast (30s) after 50% failure rate
 *   HALF_OPEN → 1 probe request; success → CLOSED, failure → OPEN again
 */
@Component
@Slf4j
public class GroqApiClient {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    @CircuitBreaker(name = "groqApi", fallbackMethod = "callFallback")
    public String call(String prompt) {
        log.debug("Calling Groq API, model={}", groqModel);
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", groqModel);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        body.put("temperature", 0.1);

        ResponseEntity<String> response = restTemplate.postForEntity(
                groqApiUrl, new HttpEntity<>(body, headers), String.class);

        log.debug("Groq API responded: status={}", response.getStatusCode());
        return response.getBody();
    }

    // Called automatically by Resilience4j when circuit is OPEN or call fails
    public String callFallback(String prompt, Exception e) {
        log.error("Groq circuit breaker triggered: {}", e.getMessage());
        throw new ServiceUnavailableException(
                "Groq API is currently unavailable. Circuit breaker is open. Try again in 30 seconds.");
    }
}
