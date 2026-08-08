package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.EmployeCompetence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EmployeCompetenceRepository extends JpaRepository<EmployeCompetence, UUID> {
    List<EmployeCompetence> findByEmployeId(UUID employeId);
}