package com.sbgs.backend_2026.dto.Stats;

import java.util.List;
import java.util.Map;

public record StatsResponsableStageResponse(
        int nouvellesDemandes,
        int demandesAcceptees,
        int stagesSansAttestation,
        int stagiairesNonAffectes,
        Map<String, Integer> stagiairesParService,
        Map<String, Integer> absencesParService,
        int rapportsDeposes,
        int rapportsNonDeposes
) {
}