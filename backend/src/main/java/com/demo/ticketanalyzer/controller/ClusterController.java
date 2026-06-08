package com.demo.ticketanalyzer.controller;

import com.demo.ticketanalyzer.dto.ClusterJobResponse;
import com.demo.ticketanalyzer.entity.ClusterJob;
import com.demo.ticketanalyzer.service.ClusterJobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * Async job endpoints for ticket clustering.
 *
 * POST /api/clusters/analyze      → 202 Accepted  {jobId, status: PENDING}
 * GET  /api/clusters/jobs/{id}    → 200 OK         {jobId, status, ...}
 *
 * Interview point: returning 202 (Accepted) vs 200 (OK) signals to
 * the client that the work is in-progress, not complete. The pollUrl
 * in the response body tells clients exactly where to check status —
 * self-documenting API design (HATEOAS-lite).
 */
@RestController
@RequestMapping("/api/clusters")
@RequiredArgsConstructor
public class ClusterController {

    private final ClusterJobService jobService;

    @PostMapping("/analyze")
    public ResponseEntity<ClusterJobResponse> analyze(Authentication auth) {
        ClusterJob job = jobService.createJob(auth.getName());
        jobService.executeJob(job.getId()); // fires async, returns immediately
        return ResponseEntity.accepted().body(ClusterJobResponse.from(job));
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<ClusterJobResponse> getJob(@PathVariable String jobId) {
        return ResponseEntity.ok(ClusterJobResponse.from(jobService.getJob(jobId)));
    }
}
