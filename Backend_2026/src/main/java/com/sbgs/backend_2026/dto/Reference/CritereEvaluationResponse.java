package com.sbgs.backend_2026.dto.Reference;

import java.util.UUID;

public record CritereEvaluationResponse(
        UUID id,
        String nom,
        UUID competenceId,   // optionnel, pour afficher la compétence associée
        String categorie   // ✅ AJOUTÉ
) {
}