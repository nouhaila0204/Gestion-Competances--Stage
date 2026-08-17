package com.sbgs.backend_2026.dto.Competences;

import java.time.LocalDate;
import java.util.List;

public record OffreEmploiCreateRequest(
        String titre, String description, LocalDate datePublication, String statut,
        List<CompetenceRequiseRequest> competencesRequises
) {
}