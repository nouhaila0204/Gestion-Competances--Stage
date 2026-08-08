package com.sbgs.backend_2026.dto.Stage;

import com.sbgs.backend_2026.entity.enums.TypeDocument;

import java.util.UUID;

public record DocumentStageResponse(
        UUID id,
        TypeDocument typeDocument,
        String nomFichier,
        String typeContenu,
        boolean present
) {
}