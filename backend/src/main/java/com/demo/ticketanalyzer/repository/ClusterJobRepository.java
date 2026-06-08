package com.demo.ticketanalyzer.repository;

import com.demo.ticketanalyzer.entity.ClusterJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClusterJobRepository extends JpaRepository<ClusterJob, String> {
}
