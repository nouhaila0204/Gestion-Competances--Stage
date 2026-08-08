package com.sbgs.backend_2026.dto.Stage;

import com.sbgs.backend_2026.entity.enums.DecisionRH;
import com.sbgs.backend_2026.entity.enums.EtatDossier;
import com.sbgs.backend_2026.entity.enums.TypeStage;

import java.time.LocalDate;
import java.util.UUID;

public record DemandeStageResponse(
        UUID id,
        String stagiaireNomComplet,
        String ecoleNom,
        TypeStage typeStage,
        EtatDossier etatDossier,
        DecisionRH decisionRh,
        LocalDate dateSoumission,
        LocalDate dateDebutSouhaitee,
        String dureeSouhaitee,
        UUID stageId
) {
}
