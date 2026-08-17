package com.sbgs.backend_2026.service.Competance;

import com.sbgs.backend_2026.dto.Competences.CandidatDetailResponse;
import com.sbgs.backend_2026.dto.Competences.CandidatMatchResponse;
import com.sbgs.backend_2026.dto.Competences.CompetenceDetailResponse;
import com.sbgs.backend_2026.dto.Competences.MatchingResultResponse;
import com.sbgs.backend_2026.entity.Competence;
import com.sbgs.backend_2026.entity.Employe;
import com.sbgs.backend_2026.entity.OffreCompetence;
import com.sbgs.backend_2026.entity.Stagiaire;
import com.sbgs.backend_2026.entity.enums.NiveauCompetence;
import com.sbgs.backend_2026.repository.EmployeRepository;
import com.sbgs.backend_2026.repository.OffreCompetenceRepository;
import com.sbgs.backend_2026.repository.OffreEmploiRepository;
import com.sbgs.backend_2026.repository.StagiaireRepository;
import com.sbgs.backend_2026.service.Competance.Matching.CandidatDataSource;
import com.sbgs.backend_2026.service.Matching.EmployeDataStrategy;
import com.sbgs.backend_2026.service.Competance.Matching.ProfilCompetences;
import com.sbgs.backend_2026.service.Competance.Matching.StagiaireDataStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class MatchingService {

    private final OffreEmploiRepository offreEmploiRepository;
    private final OffreCompetenceRepository offreCompetenceRepository;
    private final EmployeRepository employeRepository;
    private final StagiaireRepository stagiaireRepository;
    private final EmployeDataStrategy employeDataStrategy;
    private final StagiaireDataStrategy stagiaireDataStrategy;

    @Transactional(readOnly = true)
    public MatchingResultResponse matcher(UUID offreId) {
        offreEmploiRepository.findById(offreId)
                .orElseThrow(() -> new IllegalArgumentException("Offre introuvable : " + offreId));

        List<OffreCompetence> requises = offreCompetenceRepository.findByOffreEmploiId(offreId);
        if (requises.isEmpty()) {
            throw new IllegalStateException("Cette offre n'a aucune competence requise definie");
        }

        List<CandidatMatchResponse> employes = employeRepository.findAll().stream()
                .map(e -> resume(e.getId(), e.getPrenom() + " " + e.getNom(), requises, employeDataStrategy))
                .filter(c -> c.scoreTechnique() > 0 || c.possedeCompetencesNonNotees())
                .sorted(Comparator.comparingDouble(CandidatMatchResponse::scoreTechnique).reversed())
                .toList();

        if (!employes.isEmpty()) {
            return new MatchingResultResponse(employes, List.of());
        }

        List<CandidatMatchResponse> stagiaires = stagiaireRepository.findAll().stream()
                .map(s -> resume(s.getId(), s.getPrenom() + " " + s.getNom(), requises, stagiaireDataStrategy))
                .filter(c -> c.scoreTechnique() > 0 || c.possedeCompetencesNonNotees())
                .sorted(Comparator.comparingDouble(CandidatMatchResponse::scoreTechnique).reversed())
                .toList();

        return new MatchingResultResponse(List.of(), stagiaires);
    }

    @Transactional(readOnly = true)
    public CandidatDetailResponse details(UUID offreId, String type, UUID candidatId) {
        List<OffreCompetence> requises = offreCompetenceRepository.findByOffreEmploiId(offreId);
        if (requises.isEmpty()) {
            throw new IllegalArgumentException("Offre introuvable ou sans competence requise : " + offreId);
        }

        CandidatDataSource source;
        String nomComplet;

        if ("EMPLOYE".equalsIgnoreCase(type)) {
            Employe employe = employeRepository.findById(candidatId)
                    .orElseThrow(() -> new IllegalArgumentException("Employe introuvable : " + candidatId));
            source = employeDataStrategy;
            nomComplet = employe.getPrenom() + " " + employe.getNom();
        } else if ("STAGIAIRE".equalsIgnoreCase(type)) {
            Stagiaire stagiaire = stagiaireRepository.findById(candidatId)
                    .orElseThrow(() -> new IllegalArgumentException("Stagiaire introuvable : " + candidatId));
            source = stagiaireDataStrategy;
            nomComplet = stagiaire.getPrenom() + " " + stagiaire.getNom();
        } else {
            throw new IllegalArgumentException("Type de candidat invalide (attendu EMPLOYE ou STAGIAIRE) : " + type);
        }

        ProfilCompetences profil = source.collecterDonnees(candidatId);
        List<CompetenceDetailResponse> detail = construireDetail(requises, profil.niveauxConfirmes());

        List<String> nonNotees = requises.stream()
                .map(OffreCompetence::getCompetence)
                .filter(c -> !profil.niveauxConfirmes().containsKey(c.getId()))
                .filter(c -> profil.competencesNonConfirmees().contains(c.getId())
                        || profil.nomsDetectesNonLies().contains(c.getNom().toLowerCase(Locale.FRENCH)))
                .map(Competence::getNom)
                .distinct()
                .toList();

        return new CandidatDetailResponse(type.toUpperCase(), candidatId, nomComplet,
                calculerScore(detail), detail, nonNotees);
    }

    private CandidatMatchResponse resume(UUID id, String nomComplet, List<OffreCompetence> requises,
                                         CandidatDataSource source) {
        ProfilCompetences profil = source.collecterDonnees(id);
        Map<UUID, NiveauCompetence> possedeesAvecNiveau = profil.niveauxConfirmes();

        List<String> couvertes = new ArrayList<>();
        List<String> manquantes = new ArrayList<>();
        boolean possedeNonNotees = false;

        for (OffreCompetence req : requises) {
            UUID competenceId = req.getCompetence().getId();
            NiveauCompetence niveauPossede = possedeesAvecNiveau.get(competenceId);
            if (niveauPossede != null && niveauPossede.ordinal() >= req.getNiveauRequis().ordinal()) {
                couvertes.add(req.getCompetence().getNom());
            } else {
                manquantes.add(req.getCompetence().getNom());
                boolean nonConfirmee = profil.competencesNonConfirmees().contains(competenceId);
                boolean detecteeOrpheline = profil.nomsDetectesNonLies()
                        .contains(req.getCompetence().getNom().toLowerCase(Locale.FRENCH));
                if (nonConfirmee || detecteeOrpheline) {
                    possedeNonNotees = true;
                }
            }
        }

        double score = Math.round((double) couvertes.size() / requises.size() * 1000) / 10.0;
        return new CandidatMatchResponse(id, nomComplet, score, couvertes, manquantes, possedeNonNotees);
    }

    private List<CompetenceDetailResponse> construireDetail(List<OffreCompetence> requises,
                                                            Map<UUID, NiveauCompetence> possedees) {
        List<CompetenceDetailResponse> detail = new ArrayList<>();
        for (OffreCompetence req : requises) {
            NiveauCompetence niveauPossede = possedees.get(req.getCompetence().getId());
            boolean couverte = niveauPossede != null && niveauPossede.ordinal() >= req.getNiveauRequis().ordinal();
            detail.add(new CompetenceDetailResponse(
                    req.getCompetence().getNom(), req.getCompetence().getCategorie(),
                    niveauPossede, req.getNiveauRequis(), couverte));
        }
        return detail;
    }

    private double calculerScore(List<CompetenceDetailResponse> detail) {
        long couvertes = detail.stream().filter(CompetenceDetailResponse::couverte).count();
        return Math.round((double) couvertes / detail.size() * 1000) / 10.0;
    }
}