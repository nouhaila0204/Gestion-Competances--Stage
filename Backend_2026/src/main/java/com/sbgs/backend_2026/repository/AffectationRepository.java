package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AffectationRepository extends JpaRepository<Affectation, UUID> {
}
