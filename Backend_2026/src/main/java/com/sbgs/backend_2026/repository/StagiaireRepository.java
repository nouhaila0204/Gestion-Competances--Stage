package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Stagiaire;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StagiaireRepository extends JpaRepository<Stagiaire, UUID> {
}
