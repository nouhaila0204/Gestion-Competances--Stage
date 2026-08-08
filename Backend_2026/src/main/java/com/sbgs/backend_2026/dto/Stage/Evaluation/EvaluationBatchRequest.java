package com.sbgs.backend_2026.dto.Stage.Evaluation;

import java.util.List;
import java.util.UUID;

public record EvaluationBatchRequest(UUID responsableId, List<EvaluationNoteRequest> notes) {
}
