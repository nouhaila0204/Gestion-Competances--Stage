package com.sbgs.backend_2026.dto.Competences;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record OffreEmploiResponse(
        UUID id, String titre, String description, LocalDate datePublication, String statut,
        List<String> competencesRequisesLibelle
) {
}