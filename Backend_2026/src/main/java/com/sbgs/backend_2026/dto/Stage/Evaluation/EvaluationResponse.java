package com.sbgs.backend_2026.dto.Stage.Evaluation;

import java.util.UUID;

public record EvaluationResponse(
        UUID id, UUID stageId, UUID critereId, String critereNom, Integer note, String responsableNomComplet
) {
}
