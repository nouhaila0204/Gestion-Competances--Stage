package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stage.*;
import com.sbgs.backend_2026.entity.enums.TypeDocument;
import com.sbgs.backend_2026.entity.enums.TypeStage;
import com.sbgs.backend_2026.service.DemandeStageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication; // ✅ UN SEUL IMPORT
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/demandes")
@RequiredArgsConstructor
public class DemandeStageController {

    private final DemandeStageService demandeStageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DemandeStageResponse soumettre(
            Authentication authentication,  // ✅ AJOUTÉ - récupère l'utilisateur connecté
            @RequestParam UUID ecoleId,
            @RequestParam TypeStage typeStage,
            @RequestParam LocalDate dateDebutSouhaitee,
            @RequestParam String dureeSouhaitee,
            @RequestParam(required = false) MultipartFile cv,
            @RequestParam(required = false) MultipartFile cni,
            @RequestParam(required = false) List<MultipartFile> photos,
            @RequestParam(required = false) MultipartFile attestationAssurance,
            @RequestParam(required = false) MultipartFile conventionStage,
            @RequestParam(required = false) MultipartFile demandeManuscrite
    ) throws IOException {

        // ✅ Récupérer l'email du stagiaire connecté
        String emailStagiaire = authentication.getName();

        DemandeStageCreateRequest request = new DemandeStageCreateRequest(
                ecoleId, typeStage, dateDebutSouhaitee, dureeSouhaitee);

        List<PieceJointe> pieces = new ArrayList<>();
        pieces.add(new PieceJointe(TypeDocument.CV, cv));
        pieces.add(new PieceJointe(TypeDocument.CNI, cni));
        pieces.add(new PieceJointe(TypeDocument.ATTESTATION_ASSURANCE, attestationAssurance));
        pieces.add(new PieceJointe(TypeDocument.CONVENTION_STAGE, conventionStage));
        pieces.add(new PieceJointe(TypeDocument.DEMANDE_MANUSCRITE, demandeManuscrite));
        if (photos != null) {
            for (MultipartFile photo : photos) {
                pieces.add(new PieceJointe(TypeDocument.PHOTO, photo));
            }
        }

        // ✅ Passer l'email au service
        return demandeStageService.soumettreDemande(request, pieces, emailStagiaire);
    }

    @PatchMapping("/{id}/dossier")
    public DemandeStageResponse traiterDossier(@PathVariable UUID id,
                                               @RequestBody TraiterDossierRequest request) {
        return demandeStageService.traiterDossier(id, request);
    }

    @PatchMapping("/{id}/decision")
    public DemandeStageResponse decider(@PathVariable UUID id,
                                        @RequestBody DeciderRhRequest request) {
        return demandeStageService.decider(id, request);
    }

    @GetMapping
    public List<DemandeStageResponse> lister(Authentication authentication) {
        boolean estStagiaire = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAGIAIRE"));
        return estStagiaire
                ? demandeStageService.listerPourStagiaire(authentication.getName())
                : demandeStageService.lister();
    }

    @GetMapping("/{id}")
    public DemandeStageResponse obtenir(@PathVariable UUID id, Authentication authentication) {
        boolean estStagiaire = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAGIAIRE"));
        return estStagiaire
                ? demandeStageService.obtenirPourStagiaire(id, authentication.getName())
                : demandeStageService.obtenir(id);
    }

    // DemandeStageController.java
    @GetMapping("/derniere")
    public DemandeStageResponse obtenirDerniereDemande(Authentication authentication) {
        return demandeStageService.obtenirDerniereDemande(authentication.getName());
    }
}