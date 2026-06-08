package com.demo.ticketanalyzer.service;

import com.demo.ticketanalyzer.client.GroqApiClient;
import com.demo.ticketanalyzer.entity.Ticket;
import com.demo.ticketanalyzer.repository.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Orchestrates ticket clustering via Groq LLM.
 * HTTP transport delegated to GroqApiClient (which owns the circuit breaker).
 * @CacheEvict ensures the "tickets" cache is invalidated after clustering
 * so the next GET /api/tickets returns fresh data with clusterLabels set.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GroqClusteringService {

    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;
    private final GroqApiClient groqApiClient;

    @CacheEvict(value = "tickets", allEntries = true)
    public void analyzeAndCluster() {
        List<Ticket> tickets = ticketRepository.findAll();
        log.debug("Sending {} tickets to Groq for clustering", tickets.size());

        String rawResponse = groqApiClient.call(buildPrompt(tickets));
        Map<Long, String> clusterMap = parseResponse(rawResponse);
        persistLabels(tickets, clusterMap);

        log.debug("Clustering complete, cache evicted");
    }

    // ── Private helpers ──────────────────────────────────────────────────────

    private String buildPrompt(List<Ticket> tickets) {
        StringBuilder sb = new StringBuilder();
        sb.append("You are a support ticket analyst. Analyze the following tickets and group them into clusters.\n\n");
        sb.append("Return ONLY a valid JSON array with no markdown or explanation:\n");
        sb.append("[{\"id\": 1, \"cluster\": \"Authentication Issues\"}, {\"id\": 2, \"cluster\": \"Authentication Issues\"}, ...]\n\n");
        sb.append("Use consistent cluster names across all tickets. Tickets:\n\n");
        for (Ticket t : tickets) {
            sb.append(String.format("ID: %d | %s | %s%n", t.getId(), t.getTitle(), t.getDescription()));
        }
        return sb.toString();
    }

    private Map<Long, String> parseResponse(String groqResponse) {
        Map<Long, String> result = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(groqResponse);
            String content = root.path("choices").get(0)
                    .path("message").path("content").asText();
            content = content.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").trim();
            JsonNode clusters = objectMapper.readTree(content);
            for (JsonNode node : clusters) {
                result.put(node.path("id").asLong(), node.path("cluster").asText());
            }
            log.debug("Parsed {} cluster assignments", result.size());
        } catch (Exception e) {
            log.error("Failed to parse Groq response: {}", e.getMessage());
            throw new RuntimeException("Could not parse LLM response: " + e.getMessage());
        }
        return result;
    }

    private void persistLabels(List<Ticket> tickets, Map<Long, String> clusterMap) {
        for (Ticket ticket : tickets) {
            String label = clusterMap.get(ticket.getId());
            if (label != null) {
                ticket.setClusterLabel(label);
                ticketRepository.save(ticket);
            }
        }
    }
}
