package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.OffreCompetence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OffreCompetenceRepository extends JpaRepository<OffreCompetence, UUID> {
    List<OffreCompetence> findByOffreEmploiId(UUID offreEmploiId);
}
