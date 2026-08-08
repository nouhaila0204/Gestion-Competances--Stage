// repository/EvaluationRepository.java
package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EvaluationRepository extends JpaRepository<Evaluation, UUID> {
    List<Evaluation> findByStageId(UUID stageId);
    boolean existsByStageIdAndCritereId(UUID stageId, UUID critereId);
}
