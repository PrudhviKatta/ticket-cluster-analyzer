package com.demo.ticketanalyzer.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String email;
    private String role;
}
