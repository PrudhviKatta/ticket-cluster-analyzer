package com.demo.ticketanalyzer.service;

import com.demo.ticketanalyzer.entity.Ticket;
import com.demo.ticketanalyzer.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public Ticket createTicket(Ticket ticket) {
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setClusterLabel(null);
        return ticketRepository.save(ticket);
    }
}
