package com.demo.ticketanalyzer.service;

import com.demo.ticketanalyzer.entity.ClusterJob;
import com.demo.ticketanalyzer.entity.JobStatus;
import com.demo.ticketanalyzer.exception.ResourceNotFoundException;
import com.demo.ticketanalyzer.repository.ClusterJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Manages async clustering job lifecycle.
 *
 * Interview point: @Async requires calling through a Spring proxy.
 * That's why executeJob is here (separate bean from ClusterController).
 * If we called an @Async method on 'this' inside the same class,
 * Spring's proxy would be bypassed and the call would be synchronous.
 *
 * Job state machine: PENDING → PROCESSING → COMPLETED | FAILED
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ClusterJobService {

    private final ClusterJobRepository jobRepository;
    private final GroqClusteringService clusteringService;

    public ClusterJob createJob(String triggeredBy) {
        ClusterJob job = new ClusterJob(
                UUID.randomUUID().toString(),
                JobStatus.PENDING,
                LocalDateTime.now(),
                null, null,
                triggeredBy
        );
        return jobRepository.save(job);
    }

    @Async("groqTaskExecutor")
    public void executeJob(String jobId) {
        ClusterJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));

        log.debug("Starting job {} for user {}", jobId, job.getTriggeredBy());
        job.setStatus(JobStatus.PROCESSING);
        jobRepository.save(job);

        try {
            clusteringService.analyzeAndCluster();
            job.setStatus(JobStatus.COMPLETED);
            job.setCompletedAt(LocalDateTime.now());
            log.debug("Job {} completed", jobId);
        } catch (Exception e) {
            log.error("Job {} failed: {}", jobId, e.getMessage());
            job.setStatus(JobStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            job.setCompletedAt(LocalDateTime.now());
        }

        jobRepository.save(job);
    }

    public ClusterJob getJob(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job not found: " + jobId));
    }
}
