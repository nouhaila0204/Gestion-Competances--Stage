package com.sbgs.backend_2026.dto.Stage;

import java.time.LocalDate;
import java.util.UUID;

public record AffectationResponse(
        UUID id, UUID stageId, UUID serviceId, String serviceNom,
        LocalDate dateDebut, LocalDate dateFin,
        int nombreResponsablesNotifies
) {
}