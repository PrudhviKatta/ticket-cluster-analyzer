package com.demo.ticketanalyzer.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Spring naming strategy: submittedBy → submitted_by in DB
    private String submittedBy;

    private LocalDateTime createdAt;

    // null until AI analysis is run
    private String clusterLabel;
}
