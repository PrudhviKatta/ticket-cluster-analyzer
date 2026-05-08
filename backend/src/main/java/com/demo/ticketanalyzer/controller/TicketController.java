package com.demo.ticketanalyzer.controller;

import com.demo.ticketanalyzer.entity.Ticket;
import com.demo.ticketanalyzer.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    // GET /api/tickets  →  all tickets (with clusterLabel if analyzed)
    @GetMapping
    public ResponseEntity<List<Ticket>> getAll() {
        return ResponseEntity.ok(ticketService.getAllTickets());
    }

    // POST /api/tickets  →  create a new ticket
    @PostMapping
    public ResponseEntity<Ticket> create(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(ticketService.createTicket(ticket));
    }
}
