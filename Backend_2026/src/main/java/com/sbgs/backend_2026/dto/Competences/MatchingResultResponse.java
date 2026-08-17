package com.sbgs.backend_2026.dto.Competences;

import java.util.List;

public record MatchingResultResponse(
        List<CandidatMatchResponse> employes,
        List<CandidatMatchResponse> stagiaires
) {
}