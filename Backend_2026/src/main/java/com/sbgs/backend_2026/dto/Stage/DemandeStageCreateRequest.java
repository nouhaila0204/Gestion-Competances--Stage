package com.sbgs.backend_2026.dto.Stage;

import com.sbgs.backend_2026.entity.enums.TypeStage;

import java.time.LocalDate;
import java.util.UUID;

public record DemandeStageCreateRequest(
        UUID stagiaireId,
        UUID ecoleId,
        TypeStage typeStage,
        LocalDate dateDebutSouhaitee,
        String dureeSouhaitee
) {
}