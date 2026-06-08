package com.demo.ticketanalyzer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Tracks async Groq clustering jobs.
 * Interview point: DB-backed job store survives restarts and is queryable
 * (vs in-memory map which would be lost on restart).
 */
@Entity
@Table(name = "cluster_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClusterJob {

    @Id
    private String id; // UUID — set by service, not auto-generated

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime completedAt;

    @Column(columnDefinition = "TEXT")
    private String errorMessage;

    private String triggeredBy; // email of requesting user
}
