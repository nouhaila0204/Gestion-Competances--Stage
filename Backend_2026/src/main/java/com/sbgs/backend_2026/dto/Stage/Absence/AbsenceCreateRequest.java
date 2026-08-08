package com.sbgs.backend_2026.dto.Stage.Absence;

import java.time.LocalDate;
import java.util.UUID;

public record AbsenceCreateRequest(
        LocalDate date,
        String motif,
        boolean justifiee,
        boolean retard,
        UUID marqueParId
) {
}