package com.sbgs.backend_2026.dto.Stage.Absence;

import java.time.LocalDate;
import java.util.UUID;

public record AbsenceResponse(
        UUID id,
        UUID stageId,
        LocalDate date,
        String motif,
        boolean justifiee,
        boolean retard,
        String marqueParNomComplet
) {
}