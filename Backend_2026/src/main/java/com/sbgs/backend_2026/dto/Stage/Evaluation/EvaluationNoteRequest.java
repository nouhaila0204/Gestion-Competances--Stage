package com.sbgs.backend_2026.dto.Stage.Evaluation;

import java.util.UUID;

public record EvaluationNoteRequest(UUID critereId, Integer note) {
}