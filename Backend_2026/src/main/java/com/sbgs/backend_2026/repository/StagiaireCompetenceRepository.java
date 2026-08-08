package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.StagiaireCompetence;
import com.sbgs.backend_2026.entity.enums.SourceCompetence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface StagiaireCompetenceRepository extends JpaRepository<StagiaireCompetence, UUID> {
    List<StagiaireCompetence> findByStagiaireId(UUID stagiaireId);
    Optional<StagiaireCompetence> findByStagiaireIdAndCompetenceIdAndSource(
            UUID stagiaireId, UUID competenceId, SourceCompetence source);
    void deleteByStagiaireIdAndSource(UUID stagiaireId, SourceCompetence source);
}