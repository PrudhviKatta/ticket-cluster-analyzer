package com.demo.ticketanalyzer.controller;

import com.demo.ticketanalyzer.entity.Ticket;
import com.demo.ticketanalyzer.service.GroqClusteringService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clusters")
@RequiredArgsConstructor
public class ClusterController {

    private final GroqClusteringService clusteringService;

    // POST /api/clusters/analyze
    // Calls Groq, updates all ticket clusterLabels, returns updated ticket list
    @PostMapping("/analyze")
    public ResponseEntity<List<Ticket>> analyze() {
        return ResponseEntity.ok(clusteringService.analyzeAndCluster());
    }
}
