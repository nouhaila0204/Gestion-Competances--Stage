package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.DemandeStage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DemandeStageRepository extends JpaRepository<DemandeStage, UUID> {
}
