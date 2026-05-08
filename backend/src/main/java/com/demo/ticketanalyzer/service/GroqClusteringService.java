package com.demo.ticketanalyzer.service;

import com.demo.ticketanalyzer.entity.Ticket;
import com.demo.ticketanalyzer.repository.TicketRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * Sends all tickets to Groq LLM in a single request.
 * LLM returns JSON cluster assignments; we persist labels back to H2.
 *
 * Interview talking point: using LLM as a clustering engine avoids
 * the need for training data or feature engineering. The model understands
 * semantic similarity naturally.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GroqClusteringService {

    @Value("${groq.api.key}")
    private String groqApiKey;

    @Value("${groq.api.url}")
    private String groqApiUrl;

    @Value("${groq.model}")
    private String groqModel;

    private final TicketRepository ticketRepository;
    private final ObjectMapper objectMapper;

    public List<Ticket> analyzeAndCluster() {
        List<Ticket> tickets = ticketRepository.findAll();
        log.debug("Sending {} tickets to Groq for clustering", tickets.size());

        String rawResponse = callGroq(buildPrompt(tickets));
        Map<Long, String> clusterMap = parseResponse(rawResponse);

        return persistLabels(tickets, clusterMap);
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

    private String callGroq(String prompt) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(groqApiKey);

        Map<String, Object> body = new HashMap<>();
        body.put("model", groqModel);
        body.put("messages", List.of(Map.of("role", "user", "content", prompt)));
        body.put("temperature", 0.1); // low temperature = consistent cluster names

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(groqApiUrl, entity, String.class);
        return response.getBody();
    }

    private Map<Long, String> parseResponse(String groqResponse) {
        Map<Long, String> result = new HashMap<>();
        try {
            JsonNode root = objectMapper.readTree(groqResponse);
            String content = root.path("choices").get(0)
                    .path("message").path("content").asText();

            // Strip markdown code blocks if model adds them despite instructions
            content = content.replaceAll("(?s)```json\\s*", "").replaceAll("```", "").trim();

            JsonNode clusters = objectMapper.readTree(content);
            for (JsonNode node : clusters) {
                result.put(node.path("id").asLong(), node.path("cluster").asText());
            }
            log.debug("Parsed {} cluster assignments from Groq", result.size());
        } catch (Exception e) {
            log.error("Failed to parse Groq response: {}", e.getMessage());
        }
        return result;
    }

    private List<Ticket> persistLabels(List<Ticket> tickets, Map<Long, String> clusterMap) {
        for (Ticket ticket : tickets) {
            String label = clusterMap.get(ticket.getId());
            if (label != null) {
                ticket.setClusterLabel(label);
                ticketRepository.save(ticket);
            }
        }
        return ticketRepository.findAll();
    }
}
