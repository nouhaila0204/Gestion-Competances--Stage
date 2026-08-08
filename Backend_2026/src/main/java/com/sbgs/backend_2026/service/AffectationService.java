package com.sbgs.backend_2026.service;

import com.sbgs.backend_2026.dto.Stage.AffectationCreateRequest;
import com.sbgs.backend_2026.dto.Stage.AffectationResponse;
import com.sbgs.backend_2026.entity.Affectation;
import com.sbgs.backend_2026.entity.Services;
import com.sbgs.backend_2026.entity.Stage;
import com.sbgs.backend_2026.entity.enums.Role;
import com.sbgs.backend_2026.event.StageApprouveEvent;
import com.sbgs.backend_2026.repository.AffectationRepository;
import com.sbgs.backend_2026.repository.ServiceRepository;
import com.sbgs.backend_2026.repository.StageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class AffectationService {

    private final StageRepository stageRepository;
    private final ServiceRepository serviceRepository;
    private final AffectationRepository affectationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AffectationResponse affecter(UUID stageId, AffectationCreateRequest request) {
        Stage stage = stageRepository.findById(stageId)
                .orElseThrow(() -> new IllegalArgumentException("Stage introuvable : " + stageId));
        Services service = serviceRepository.findById(request.serviceId())
                .orElseThrow(() -> new IllegalArgumentException("Service introuvable : " + request.serviceId()));

        Affectation affectation = Affectation.builder()
                .stage(stage)
                .service(service)
                .dateDebut(request.dateDebut())
                .dateFin(request.dateFin())
                .build();

        stage.getAffectations().add(affectation);

        LocalDate debutMin = stage.getAffectations().stream()
                .map(Affectation::getDateDebut).min(LocalDate::compareTo).orElse(stage.getDateDebut());
        LocalDate finMax = stage.getAffectations().stream()
                .map(Affectation::getDateFin).max(LocalDate::compareTo).orElse(stage.getDateFin());
        stage.setDateDebut(debutMin);
        stage.setDateFin(finMax);

        stage = stageRepository.save(stage);
        Affectation saved = stage.getAffectations().get(stage.getAffectations().size() - 1);

        var stagiaire = stage.getDemandeStage().getStagiaire();
        List<String> emailsResponsables = service.getPersonnels().stream()
                .filter(p -> p.getUtilisateur().getRole() == Role.RESPONSABLE_SERVICE)
                .map(p -> p.getUtilisateur().getEmail())
                .toList();

        if (emailsResponsables.isEmpty()) {
            log.warn("Aucun RESPONSABLE_SERVICE trouve pour le service {} ({}) -- affectation creee, "
                    + "mais aucun email envoye cote responsable.", service.getNom(), service.getId());
        }

        eventPublisher.publishEvent(new StageApprouveEvent(
                stagiaire.getUtilisateur().getEmail(),
                stagiaire.getPrenom(),
                stagiaire.getPrenom() + " " + stagiaire.getNom(),
                service.getNom(),
                request.dateDebut(),
                request.dateFin(),
                emailsResponsables
        ));

        return toResponse(saved, emailsResponsables.size());
    }

    private AffectationResponse toResponse(Affectation a, int nombreResponsablesNotifies) {
        return new AffectationResponse(
                a.getId(), a.getStage().getId(), a.getService().getId(), a.getService().getNom(),
                a.getDateDebut(), a.getDateFin(), nombreResponsablesNotifies);
    }
}