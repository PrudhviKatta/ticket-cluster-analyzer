package com.demo.ticketanalyzer.repository;

import com.demo.ticketanalyzer.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
}
