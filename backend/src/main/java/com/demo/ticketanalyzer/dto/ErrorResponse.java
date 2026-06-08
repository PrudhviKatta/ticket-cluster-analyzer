package com.demo.ticketanalyzer.dto;

import java.time.Instant;

/**
 * Standard error envelope for every non-2xx response.
 * Interview point: consistent error shape lets clients handle errors generically.
 */
public record ErrorResponse(String code, String message, String timestamp) {

    public ErrorResponse(String code, String message) {
        this(code, message, Instant.now().toString());
    }
}
