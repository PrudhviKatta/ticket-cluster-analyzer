package com.demo.ticketanalyzer.dto;

import com.demo.ticketanalyzer.entity.ClusterJob;
import com.demo.ticketanalyzer.entity.JobStatus;
import java.time.LocalDateTime;

/**
 * Returned immediately from POST /api/clusters/analyze (202 Accepted).
 * Client polls GET /api/clusters/jobs/{jobId} until status = COMPLETED.
 */
public record ClusterJobResponse(
        String jobId,
        String status,
        LocalDateTime createdAt,
        LocalDateTime completedAt,
        String errorMessage,
        String message,
        String pollUrl
) {
    public static ClusterJobResponse from(ClusterJob job) {
        return new ClusterJobResponse(
                job.getId(),
                job.getStatus().name(),
                job.getCreatedAt(),
                job.getCompletedAt(),
                job.getErrorMessage(),
                describeStatus(job.getStatus()),
                "/api/clusters/jobs/" + job.getId()
        );
    }

    private static String describeStatus(JobStatus status) {
        return switch (status) {
            case PENDING     -> "Job queued, waiting for worker thread.";
            case PROCESSING  -> "Calling Groq LLM, analysing tickets...";
            case COMPLETED   -> "Done. Fetch /api/tickets for clustered results.";
            case FAILED      -> "Analysis failed. See errorMessage for details.";
        };
    }
}
