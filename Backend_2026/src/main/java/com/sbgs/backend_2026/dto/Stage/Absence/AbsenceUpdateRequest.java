package com.sbgs.backend_2026.dto.Stage.Absence;

import java.time.LocalDate;

public record AbsenceUpdateRequest(
        LocalDate date,
        String motif,
        Boolean justifiee,
        Boolean retard
) {
}