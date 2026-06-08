package com.demo.ticketanalyzer.controller;

import com.demo.ticketanalyzer.dto.TicketResponse;
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

    @GetMapping
    public ResponseEntity<List<TicketResponse>> getAll() {
        List<TicketResponse> response = ticketService.getAllTickets()
                .stream()
                .map(TicketResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TicketResponse> create(@RequestBody Ticket ticket) {
        return ResponseEntity.ok(TicketResponse.from(ticketService.createTicket(ticket)));
    }
}
