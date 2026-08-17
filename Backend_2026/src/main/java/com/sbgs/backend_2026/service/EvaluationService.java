package com.sbgs.backend_2026.service;

import com.sbgs.backend_2026.dto.Stage.Evaluation.EvaluationBatchRequest;
import com.sbgs.backend_2026.dto.Stage.Evaluation.EvaluationNoteRequest;
import com.sbgs.backend_2026.dto.Stage.Evaluation.EvaluationResponse;
import com.sbgs.backend_2026.dto.Stage.Evaluation.EvaluationUpdateRequest;
import com.sbgs.backend_2026.entity.CritereEvaluation;
import com.sbgs.backend_2026.entity.Evaluation;
import com.sbgs.backend_2026.entity.Personnel;
import com.sbgs.backend_2026.entity.Stage;
import com.sbgs.backend_2026.entity.StagiaireCompetence;
import com.sbgs.backend_2026.entity.enums.SourceCompetence;
import com.sbgs.backend_2026.repository.CritereEvaluationRepository;
import com.sbgs.backend_2026.repository.EvaluationRepository;
import com.sbgs.backend_2026.repository.PersonnelRepository;
import com.sbgs.backend_2026.repository.StageRepository;
import com.sbgs.backend_2026.repository.StagiaireCompetenceRepository;
import com.sbgs.backend_2026.service.Competance.Matching.NiveauCompetenceConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/*ajout de methe synchronise pour tranformer les notes ds evaluation em des 4 paliers ds StagiaireCompetance */

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final StageRepository stageRepository;
    private final CritereEvaluationRepository critereEvaluationRepository;
    private final PersonnelRepository personnelRepository;
    private final EvaluationRepository evaluationRepository;
    private final StagiaireCompetenceRepository stagiaireCompetenceRepository;

    @Transactional
    public List<EvaluationResponse> noter(UUID stageId, EvaluationBatchRequest request) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Stage introuvable : " + stageId));
        Personnel responsable = personnelRepository.findById(request.responsableId())
                .orElseThrow(() -> new IllegalArgumentException("Responsable introuvable : " + request.responsableId()));

        List<Evaluation> nouvelles = new ArrayList<>();
        for (EvaluationNoteRequest noteRequest : request.notes()) {
            if (evaluationRepository.existsByStageIdAndCritereId(stageId, noteRequest.critereId())) {
                throw new IllegalStateException(
                        "Ce critere a deja ete note pour ce stage : " + noteRequest.critereId());
            }
            CritereEvaluation critere = critereEvaluationRepository.findById(noteRequest.critereId())
                    .orElseThrow(() -> new IllegalArgumentException("Critere introuvable : " + noteRequest.critereId()));

            Evaluation evaluation = Evaluation.builder()
                    .stage(stage)
                    .critere(critere)
                    .responsable(responsable)
                    .note(noteRequest.note())
                    .build();
            nouvelles.add(evaluation);

            if (critere.getCompetence() != null) {
                synchroniserCompetenceStagiaire(stage, critere, noteRequest.note());
            }
        }

        stage.getEvaluations().addAll(nouvelles);
        stageRepository.save(stage);

        return nouvelles.stream().map(this::toResponse).collect(Collectors.toList());
    }

    private void synchroniserCompetenceStagiaire(Stage stage, CritereEvaluation critere, int note) {
        var stagiaire = stage.getDemandeStage().getStagiaire();
        var competence = critere.getCompetence();

        StagiaireCompetence sc = stagiaireCompetenceRepository
                .findByStagiaireIdAndCompetenceIdAndSource(stagiaire.getId(), competence.getId(), SourceCompetence.EVALUATION)
                .orElseGet(() -> StagiaireCompetence.builder()
                        .stagiaire(stagiaire)
                        .competence(competence)
                        .source(SourceCompetence.EVALUATION)
                        .build());

        sc.setNiveau(NiveauCompetenceConverter.depuisNote(note));

        stagiaireCompetenceRepository.save(sc);
    }

    @Transactional
    public EvaluationResponse modifierNote(UUID evaluationId, EvaluationUpdateRequest request) {
        Evaluation evaluation = evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new IllegalArgumentException("Evaluation introuvable : " + evaluationId));
        evaluation.setNote(request.note());

        if (evaluation.getCritere().getCompetence() != null) {
            synchroniserCompetenceStagiaire(evaluation.getStage(), evaluation.getCritere(), request.note());
        }

        return toResponse(evaluationRepository.save(evaluation));
    }

    @Transactional(readOnly = true)
    public List<EvaluationResponse> lister(UUID stageId) {
        return evaluationRepository.findByStageId(stageId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private EvaluationResponse toResponse(Evaluation e) {
        return new EvaluationResponse(
                e.getId(), e.getStage().getId(), e.getCritere().getId(), e.getCritere().getNom(), e.getNote(),
                e.getResponsable().getPrenom() + " " + e.getResponsable().getNom());
    }
}