package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.dto.Stage.DocumentStageResponse;
import com.sbgs.backend_2026.entity.DocumentStage;
import com.sbgs.backend_2026.entity.enums.TypeDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface DocumentStageRepository extends JpaRepository<DocumentStage, UUID> {

    @Query("""
            SELECT new com.sbgs.backend_2026.dto.Stage.DocumentStageResponse(
                d.id, d.typeDocument, d.nomFichier, d.typeContenu, d.present)
            FROM DocumentStage d
            WHERE d.demandeStage.id = :demandeId
            """)
    List<DocumentStageResponse> findMetadataByDemandeId(@Param("demandeId") UUID demandeId);

    Optional<DocumentStage> findByDemandeStageIdAndTypeDocument(UUID demandeStageId, TypeDocument typeDocument);
}