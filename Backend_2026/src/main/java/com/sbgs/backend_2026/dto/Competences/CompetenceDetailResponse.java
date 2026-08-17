package com.sbgs.backend_2026.dto.Competences;

import com.sbgs.backend_2026.entity.enums.CategorieCompetence;
import com.sbgs.backend_2026.entity.enums.NiveauCompetence;

public record CompetenceDetailResponse(
        String nom,
        CategorieCompetence categorie,
        NiveauCompetence niveauPossede,
        NiveauCompetence niveauRequis,
        boolean couverte
) {
}