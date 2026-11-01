package com.sbgs.backend_2026.service;

import com.sbgs.backend_2026.dto.Stats.StatsResponsableStageResponse;
import com.sbgs.backend_2026.entity.enums.EtatDossier;
import com.sbgs.backend_2026.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DemandeStageRepository demandeStageRepository;
    private final StageRepository stageRepository;
    private final AffectationRepository affectationRepository;
    private final AbsenceRepository absenceRepository;

    @Transactional(readOnly = true)
    public StatsResponsableStageResponse getStatsResponsableStage() {
        int nouvellesDemandes = (int) demandeStageRepository.countByEtatDossier(EtatDossier.NOUVELLE);
        int demandesAcceptees = (int) demandeStageRepository.countByEtatDossier(EtatDossier.ACCEPTEE);

        int stagesSansAttestation = (int) stageRepository.countByDateGenerationAttestationIsNull();
        int stagiairesNonAffectes = (int) stageRepository.countByAffectationsIsEmpty();

        // ✅ Construire les Maps manuellement
        Map<String, Integer> stagiairesParService = toMap(affectationRepository.countStagiairesParServiceRaw());
        Map<String, Integer> absencesParService = toMap(absenceRepository.countAbsencesParServiceRaw());

        int rapportsDeposes = (int) stageRepository.countByRapportNomFichierIsNotNull();
        int rapportsNonDeposes = (int) stageRepository.countByRapportNomFichierIsNull();

        return new StatsResponsableStageResponse(
                nouvellesDemandes,
                demandesAcceptees,
                stagesSansAttestation,
                stagiairesNonAffectes,
                stagiairesParService,
                absencesParService,
                rapportsDeposes,
                rapportsNonDeposes
        );
    }

    // ✅ Convertit List<Object[]> en Map<String, Integer>
    private Map<String, Integer> toMap(List<Object[]> rows) {
        Map<String, Integer> map = new LinkedHashMap<>();
        for (Object[] row : rows) {
            String key = (String) row[0];
            Long value = (Long) row[1];
            map.put(key, value.intValue());
        }
        return map;
    }
}