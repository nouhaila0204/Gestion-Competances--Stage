package com.sbgs.backend_2026.service;

import com.sbgs.backend_2026.dto.Stage.DeciderRhRequest;
import com.sbgs.backend_2026.dto.Stage.DemandeStageCreateRequest;
import com.sbgs.backend_2026.dto.Stage.DemandeStageResponse;
import com.sbgs.backend_2026.dto.Stage.PieceJointe;
import com.sbgs.backend_2026.dto.Stage.TraiterDossierRequest;
import com.sbgs.backend_2026.entity.DemandeStage;
import com.sbgs.backend_2026.entity.DocumentStage;
import com.sbgs.backend_2026.entity.Ecole;
import com.sbgs.backend_2026.entity.Personnel;
import com.sbgs.backend_2026.entity.Stage;
import com.sbgs.backend_2026.entity.Stagiaire;
import com.sbgs.backend_2026.entity.enums.DecisionRH;
import com.sbgs.backend_2026.entity.enums.EtatDossier;
import com.sbgs.backend_2026.entity.enums.Role;
import com.sbgs.backend_2026.entity.enums.TypeDocument;
import com.sbgs.backend_2026.event.DecisionRhRefuseeEvent;
import com.sbgs.backend_2026.event.DossierAccepteEvent;
import com.sbgs.backend_2026.event.DossierRejeteEvent;
import com.sbgs.backend_2026.event.NewDemandeEvent;
import com.sbgs.backend_2026.repository.DemandeStageRepository;
import com.sbgs.backend_2026.repository.EcoleRepository;
import com.sbgs.backend_2026.repository.PersonnelRepository;
import com.sbgs.backend_2026.repository.StagiaireRepository;
import com.sbgs.backend_2026.service.workflow.WorkflowStage;
import com.sbgs.backend_2026.service.workflow.DocumentManquantException;
import com.sbgs.backend_2026.service.workflow.DossierChecklist;
import com.sbgs.backend_2026.service.workflow.DureeParser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class DemandeStageService {

    private final DemandeStageRepository demandeStageRepository;
    private final StagiaireRepository stagiaireRepository;
    private final EcoleRepository ecoleRepository;
    private final PersonnelRepository personnelRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public DemandeStageResponse soumettreDemande(DemandeStageCreateRequest request,
                                                 List<PieceJointe> pieces) throws IOException {

        Map<TypeDocument, Integer> manquantes = DossierChecklist.piecesManquantes(pieces);
        if (!manquantes.isEmpty()) {
            throw new DocumentManquantException(manquantes);
        }

        Stagiaire stagiaire = stagiaireRepository.findById(request.stagiaireId())
                .orElseThrow(() -> new IllegalArgumentException("Stagiaire introuvable : " + request.stagiaireId()));
        Ecole ecole = ecoleRepository.findById(request.ecoleId())
                .orElseThrow(() -> new IllegalArgumentException("Ecole introuvable : " + request.ecoleId()));

        DemandeStage demande = DemandeStage.builder()
                .stagiaire(stagiaire)
                .ecole(ecole)
                .typeStage(request.typeStage())
                .dateDebutSouhaitee(request.dateDebutSouhaitee())
                .dureeSouhaitee(request.dureeSouhaitee())
                .build();

        List<DocumentStage> documents = new ArrayList<>();
        for (PieceJointe piece : pieces) {
            if (piece.estFournie()) {
                documents.add(DocumentStage.builder()
                        .demandeStage(demande)
                        .typeDocument(piece.type())
                        .fichier(piece.fichier().getBytes())
                        .nomFichier(piece.fichier().getOriginalFilename())
                        .typeContenu(piece.fichier().getContentType())
                        .present(true)
                        .build());
            }
        }
        demande.setDocuments(documents);
        demande = demandeStageRepository.save(demande);

        List<String> emailsResponsablesStage = personnelRepository.findByUtilisateur_Role(Role.RESPONSABLE_STAGE)
                .stream().map(p -> p.getUtilisateur().getEmail()).toList();
        if (emailsResponsablesStage.isEmpty()) {
            log.warn("Aucun RESPONSABLE_STAGE trouve en base -- personne notifie pour la demande {}", demande.getId());
        }
        eventPublisher.publishEvent(new NewDemandeEvent(
                demande.getId(),
                stagiaire.getPrenom() + " " + stagiaire.getNom(),
                String.valueOf(request.typeStage()),
                emailsResponsablesStage
        ));

        return toResponse(demande);
    }

    @Transactional
    public DemandeStageResponse traiterDossier(UUID demandeId, TraiterDossierRequest request) {
        DemandeStage demande = demandeStageRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + demandeId));

        WorkflowStage.validerTransitionDossier(demande.getEtatDossier());
        demande.setEtatDossier(request.decision());
        demande = demandeStageRepository.save(demande);

        String email = demande.getStagiaire().getUtilisateur().getEmail();
        String prenom = demande.getStagiaire().getPrenom();

        if (request.decision() == EtatDossier.ACCEPTEE) {
            List<String> emailsDirecteurRh = personnelRepository.findByUtilisateur_Role(Role.DIRECTEUR_RH)
                    .stream().map(p -> p.getUtilisateur().getEmail()).toList();
            if (emailsDirecteurRh.isEmpty()) {
                log.warn("Aucun DIRECTEUR_RH trouve en base -- personne notifie pour la demande {}", demande.getId());
            }
            eventPublisher.publishEvent(new DossierAccepteEvent(email, prenom, demande.getId(), emailsDirecteurRh));
        } else {
            eventPublisher.publishEvent(new DossierRejeteEvent(email, prenom));
        }

        return toResponse(demande);
    }

    @Transactional
    public DemandeStageResponse decider(UUID demandeId, DeciderRhRequest request) {
        DemandeStage demande = demandeStageRepository.findById(demandeId)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + demandeId));

        WorkflowStage.validerTransitionDecisionRh(demande.getEtatDossier());
        demande.setDecisionRh(request.decision());

        if (request.decision() == DecisionRH.APPROUVEE) {
            Personnel responsableStage = personnelRepository.findById(request.responsableStageId())
                    .orElseThrow(() -> new IllegalArgumentException("Responsable introuvable : " + request.responsableStageId()));

            LocalDate dateFin = DureeParser
                    .calculerDateFin(demande.getDateDebutSouhaitee(), demande.getDureeSouhaitee())
                    .orElse(null);

            Stage stage = Stage.builder()
                    .demandeStage(demande)
                    .responsableStage(responsableStage)
                    .dateDebut(demande.getDateDebutSouhaitee())
                    .dateFin(dateFin)
                    .build();
            demande.setStage(stage);
        } else {
            String email = demande.getStagiaire().getUtilisateur().getEmail();
            String prenom = demande.getStagiaire().getPrenom();
            eventPublisher.publishEvent(new DecisionRhRefuseeEvent(email, prenom));
        }

        return toResponse(demandeStageRepository.save(demande));
    }

    @Transactional(readOnly = true)
    public List<DemandeStageResponse> lister() {
        return demandeStageRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DemandeStageResponse obtenir(UUID id) {
        return demandeStageRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Demande introuvable : " + id));
    }

    private DemandeStageResponse toResponse(DemandeStage d) {
        return new DemandeStageResponse(
                d.getId(),
                d.getStagiaire().getPrenom() + " " + d.getStagiaire().getNom(),
                d.getEcole().getNom(),
                d.getTypeStage(),
                d.getEtatDossier(),
                d.getDecisionRh(),
                d.getDateSoumission(),
                d.getDateDebutSouhaitee(),
                d.getDureeSouhaitee(),
                d.getStage() != null ? d.getStage().getId() : null
        );
    }
}