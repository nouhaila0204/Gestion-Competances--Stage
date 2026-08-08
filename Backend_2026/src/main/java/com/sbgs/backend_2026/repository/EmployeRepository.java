package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Employe;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeRepository extends JpaRepository<Employe, UUID> {
}
