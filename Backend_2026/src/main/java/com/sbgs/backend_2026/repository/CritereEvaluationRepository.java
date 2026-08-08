package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.CritereEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CritereEvaluationRepository extends JpaRepository<CritereEvaluation, UUID> {
}
