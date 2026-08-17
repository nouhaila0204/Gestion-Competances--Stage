package com.sbgs.backend_2026.service.Competance.Matching;

import java.util.UUID;

/**
 * Pattern Strategy : chaque implementation sait collecter le profil de
 * competences d'un type de candidat different, mais toutes exposent la
 * meme methode -- MatchingService n'a jamais besoin de savoir QUI est le
 * candidat, juste QUOI il possede.
 */
public interface CandidatDataSource {
    ProfilCompetences collecterDonnees(UUID candidatId);
}