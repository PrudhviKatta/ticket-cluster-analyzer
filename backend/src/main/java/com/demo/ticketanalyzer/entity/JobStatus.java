package com.demo.ticketanalyzer.entity;

public enum JobStatus {
    PENDING,     // job created, waiting for thread
    PROCESSING,  // Groq API call in progress
    COMPLETED,   // tickets clustered, clusterLabel persisted
    FAILED       // Groq call failed or circuit open
}
