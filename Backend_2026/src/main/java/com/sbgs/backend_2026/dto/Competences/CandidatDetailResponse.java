package com.sbgs.backend_2026.dto.Competences;

import java.util.List;
import java.util.UUID;

public record CandidatDetailResponse(
        String type,
        UUID id,
        String nomComplet,
        double scoreTechnique,
        List<CompetenceDetailResponse> competencesEvaluees,
        List<String> competencesDetecteesNonNotees
) {
}