package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.dto.Stage.StageResponse;
import com.sbgs.backend_2026.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface StageRepository extends JpaRepository<Stage, UUID> {

    @Query("""
            SELECT new com.sbgs.backend_2026.dto.Stage.StageResponse(
                s.id, s.dateDebut, s.dateFin, s.dateGenerationAttestation,
                s.attestationRemise, s.rapportNomFichier, s.rapportTypeContenu,
                s.rapportDateDepot, s.themeStage, s.responsableStage.id)
            FROM Stage s
            """)
    List<StageResponse> findAllMetadata();

    @Query("""
            SELECT new com.sbgs.backend_2026.dto.Stage.StageResponse(
                s.id, s.dateDebut, s.dateFin, s.dateGenerationAttestation,
                s.attestationRemise, s.rapportNomFichier, s.rapportTypeContenu,
                s.rapportDateDepot, s.themeStage, s.responsableStage.id)
            FROM Stage s
            WHERE s.responsableStage.id = :responsableStageId
            """)
    List<StageResponse> findMetadataByResponsableStageId(@Param("responsableStageId") UUID responsableStageId);
}
