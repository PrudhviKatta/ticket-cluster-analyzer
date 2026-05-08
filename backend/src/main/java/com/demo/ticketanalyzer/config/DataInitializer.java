package com.demo.ticketanalyzer.config;

import com.demo.ticketanalyzer.entity.User;
import com.demo.ticketanalyzer.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Creates the demo admin user on startup using the real PasswordEncoder bean,
 * so the BCrypt hash is always correct regardless of environment.
 * Demo credentials: admin@demo.com / password
 */
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.findByEmail("admin@demo.com").isEmpty()) {
            User admin = new User(null, "admin@demo.com", passwordEncoder.encode("password"), "ADMIN");
            userRepository.save(admin);
        }
    }
}
