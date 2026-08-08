package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AbsenceRepository extends JpaRepository<Absence, UUID> {
    List<Absence> findByStageId(UUID stageId);
    boolean existsByStageIdAndDate(UUID stageId, LocalDate date);
}