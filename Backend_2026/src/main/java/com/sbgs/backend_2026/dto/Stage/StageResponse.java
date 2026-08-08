package com.sbgs.backend_2026.dto.Stage;

import java.time.LocalDate;
import java.util.UUID;

public record StageResponse(
        UUID id,
        LocalDate dateDebut,
        LocalDate dateFin,
        LocalDate dateGenerationAttestation,
        boolean attestationRemise,
        String rapportNomFichier,
        String rapportTypeContenu,
        LocalDate rapportDateDepot,
        String themeStage,
        UUID responsableStageId
) {
}
