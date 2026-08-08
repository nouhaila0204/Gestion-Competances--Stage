package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Services;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ServiceRepository extends JpaRepository<Services, UUID> {
}
