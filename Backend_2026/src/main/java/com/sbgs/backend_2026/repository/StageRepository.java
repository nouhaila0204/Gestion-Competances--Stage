package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface StageRepository extends JpaRepository<Stage, UUID> {
    List<Stage> findByResponsableStageId(UUID responsableStageId);
    List<Stage> findByAffectations_Service_Id(UUID serviceId);
}