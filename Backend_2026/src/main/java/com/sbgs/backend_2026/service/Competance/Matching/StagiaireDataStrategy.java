package com.sbgs.backend_2026.service.Competance.Matching;

import com.sbgs.backend_2026.entity.StagiaireCompetence;
import com.sbgs.backend_2026.entity.enums.NiveauCompetence;
import com.sbgs.backend_2026.repository.StagiaireCompetenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class StagiaireDataStrategy implements CandidatDataSource {

    private final StagiaireCompetenceRepository stagiaireCompetenceRepository;

    @Override
    public ProfilCompetences collecterDonnees(UUID candidatId) {
        List<StagiaireCompetence> toutes = stagiaireCompetenceRepository.findByStagiaireId(candidatId);

        Map<UUID, NiveauCompetence> confirmees = new HashMap<>();
        Set<UUID> nonConfirmees = new HashSet<>();
        Set<String> nomsDetectesNonLies = new HashSet<>();

        for (StagiaireCompetence sc : toutes) {
            if (sc.getCompetence() != null) {
                if (sc.getNiveau() != null) {
                    confirmees.put(sc.getCompetence().getId(), sc.getNiveau());
                } else {
                    nonConfirmees.add(sc.getCompetence().getId());
                }
            } else if (sc.getNomDetecte() != null) {
                nomsDetectesNonLies.add(sc.getNomDetecte().toLowerCase(Locale.FRENCH));
            }
        }

        return new ProfilCompetences(confirmees, nonConfirmees, nomsDetectesNonLies);
    }
}