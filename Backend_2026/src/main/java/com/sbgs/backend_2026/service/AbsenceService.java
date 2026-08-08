package com.sbgs.backend_2026.service;

import com.sbgs.backend_2026.dto.Stage.Absence.AbsenceCreateRequest;
import com.sbgs.backend_2026.dto.Stage.Absence.AbsenceResponse;
import com.sbgs.backend_2026.dto.Stage.Absence.AbsenceUpdateRequest;
import com.sbgs.backend_2026.entity.Absence;
import com.sbgs.backend_2026.entity.Personnel;
import com.sbgs.backend_2026.entity.Stage;
import com.sbgs.backend_2026.repository.AbsenceRepository;
import com.sbgs.backend_2026.repository.PersonnelRepository;
import com.sbgs.backend_2026.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AbsenceService {

    private final StageRepository stageRepository;
    private final AbsenceRepository absenceRepository;
    private final PersonnelRepository personnelRepository;

    @Transactional
    public AbsenceResponse marquer(UUID stageId, AbsenceCreateRequest request) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Stage introuvable : " + stageId));

        if (absenceRepository.existsByStageIdAndDate(stageId, request.date())) {
            throw new IllegalStateException(
                    "Une absence est deja enregistree pour ce stage a la date du " + request.date());
        }

        Personnel marquePar = null;
        if (request.marqueParId() != null) {
            marquePar = personnelRepository.findById(request.marqueParId())
                    .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable : " + request.marqueParId()));
        }

        Absence absence = Absence.builder()
                .stage(stage)
                .marquePar(marquePar)
                .date(request.date())
                .motif(request.motif())
                .justifiee(request.justifiee())
                .retard(request.retard())
                .build();

        stage.getAbsences().add(absence);
        stage = stageRepository.save(stage);
        Absence saved = stage.getAbsences().get(stage.getAbsences().size() - 1);

        return toResponse(saved);
    }

    @Transactional
    public AbsenceResponse modifier(UUID absenceId, AbsenceUpdateRequest request) {
        Absence absence = absenceRepository.findById(absenceId)
                .orElseThrow(() -> new IllegalArgumentException("Absence introuvable : " + absenceId));

        if (request.date() != null) {
            if (!absence.getDate().equals(request.date())
                    && absenceRepository.existsByStageIdAndDate(absence.getStage().getId(), request.date())) {
                throw new IllegalStateException(
                        "Une absence est deja enregistree pour ce stage a la date du " + request.date());
            }
            absence.setDate(request.date());
        }
        if (request.motif() != null) {
            absence.setMotif(request.motif());
        }
        if (request.justifiee() != null) {
            absence.setJustifiee(request.justifiee());
        }
        if (request.retard() != null) {
            absence.setRetard(request.retard());
        }

        return toResponse(absenceRepository.save(absence));
    }

    @Transactional(readOnly = true)
    public List<AbsenceResponse> lister(UUID stageId) {
        return absenceRepository.findByStageId(stageId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private AbsenceResponse toResponse(Absence a) {
        return new AbsenceResponse(
                a.getId(),
                a.getStage().getId(),
                a.getDate(),
                a.getMotif(),
                a.isJustifiee(),
                a.isRetard(),
                a.getMarquePar() != null ? a.getMarquePar().getPrenom() + " " + a.getMarquePar().getNom() : null
        );
    }


    @Transactional
    public void supprimer(UUID absenceId) {
        if (!absenceRepository.existsById(absenceId)) {
            throw new IllegalArgumentException("Absence introuvable : " + absenceId);
        }
        absenceRepository.deleteById(absenceId);
    }
}