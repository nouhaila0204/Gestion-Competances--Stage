package com.sbgs.backend_2026.service.Competance.Matching;

import com.sbgs.backend_2026.entity.enums.NiveauCompetence;

public final class NiveauCompetenceConverter {

    private NiveauCompetenceConverter() {
    }

    public static NiveauCompetence depuisNote(int note) {
        if (note >= 16) return NiveauCompetence.EXPERT;
        if (note >= 12) return NiveauCompetence.AVANCE;
        if (note >= 8) return NiveauCompetence.INTERMEDIAIRE;
        return NiveauCompetence.DEBUTANT;
    }
}