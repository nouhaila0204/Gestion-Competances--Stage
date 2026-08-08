package com.sbgs.backend_2026.event;

import java.util.List;
import java.util.UUID;

public record NewDemandeEvent(
        UUID demandeId,
        String stagiaireNomComplet,
        String typeStage,
        List<String> emailsResponsablesStage
) {
}