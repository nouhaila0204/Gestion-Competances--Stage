package com.sbgs.backend_2026.dto.Stage;

import com.sbgs.backend_2026.entity.enums.DecisionRH;

import java.util.UUID;

public record DeciderRhRequest(DecisionRH decision, UUID responsableStageId) {
}