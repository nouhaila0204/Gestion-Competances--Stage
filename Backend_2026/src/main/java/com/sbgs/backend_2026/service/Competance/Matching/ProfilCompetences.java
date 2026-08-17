package com.sbgs.backend_2026.service.Competance.Matching;

import com.sbgs.backend_2026.entity.enums.NiveauCompetence;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public record ProfilCompetences(
        Map<UUID, NiveauCompetence> niveauxConfirmes,
        Set<UUID> competencesNonConfirmees,
        Set<String> nomsDetectesNonLies
) {
}