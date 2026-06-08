package com.demo.ticketanalyzer.dto;

import com.demo.ticketanalyzer.entity.Ticket;
import java.time.LocalDateTime;

/**
 * API contract for Ticket — decouples HTTP response shape from JPA entity.
 * Interview point: never expose JPA entities directly; adding a DB column
 * should not change the API contract.
 */
public record TicketResponse(
        Long id,
        String title,
        String description,
        String submittedBy,
        LocalDateTime createdAt,
        String clusterLabel
) {
    public static TicketResponse from(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getTitle(),
                ticket.getDescription(),
                ticket.getSubmittedBy(),
                ticket.getCreatedAt(),
                ticket.getClusterLabel()
        );
    }
}
