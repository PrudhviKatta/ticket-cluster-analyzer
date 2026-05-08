package com.demo.ticketanalyzer.service;

import com.demo.ticketanalyzer.dto.*;
import com.demo.ticketanalyzer.entity.User;
import com.demo.ticketanalyzer.repository.UserRepository;
import com.demo.ticketanalyzer.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {
        // Throws BadCredentialsException if wrong — Spring Security handles it
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = tokenProvider.generateToken(user.getEmail());
        return new LoginResponse(token, user.getEmail(), user.getRole());
    }
}
