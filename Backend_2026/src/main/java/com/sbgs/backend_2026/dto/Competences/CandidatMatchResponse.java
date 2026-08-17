package com.sbgs.backend_2026.dto.Competences;

import java.util.List;
import java.util.UUID;

public record CandidatMatchResponse(
        UUID id,
        String nomComplet,
        double scoreTechnique,
        List<String> competencesCouvertes,
        List<String> competencesManquantes,
        boolean possedeCompetencesNonNotees
) {
}