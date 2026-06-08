package com.demo.ticketanalyzer.service;

import com.demo.ticketanalyzer.entity.Ticket;
import com.demo.ticketanalyzer.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    // Cache the full ticket list — evicted on new ticket or after clustering
    @Cacheable("tickets")
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    // New ticket invalidates the cached list so it's re-fetched from DB
    @CacheEvict(value = "tickets", allEntries = true)
    public Ticket createTicket(Ticket ticket) {
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setClusterLabel(null);
        return ticketRepository.save(ticket);
    }
}
