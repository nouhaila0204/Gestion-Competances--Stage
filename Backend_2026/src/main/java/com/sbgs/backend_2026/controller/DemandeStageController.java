package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stage.DeciderRhRequest;
import com.sbgs.backend_2026.dto.Stage.DemandeStageCreateRequest;
import com.sbgs.backend_2026.dto.Stage.DemandeStageResponse;
import com.sbgs.backend_2026.dto.Stage.PieceJointe;
import com.sbgs.backend_2026.dto.Stage.TraiterDossierRequest;
import com.sbgs.backend_2026.entity.enums.TypeDocument;
import com.sbgs.backend_2026.entity.enums.TypeStage;
import com.sbgs.backend_2026.service.DemandeStageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
            @RequestParam UUID stagiaireId,
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

        DemandeStageCreateRequest request = new DemandeStageCreateRequest(
                stagiaireId, ecoleId, typeStage, dateDebutSouhaitee, dureeSouhaitee);

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

        return demandeStageService.soumettreDemande(request, pieces);
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
    public List<DemandeStageResponse> lister() {
        return demandeStageService.lister();
    }

    @GetMapping("/{id}")
    public DemandeStageResponse obtenir(@PathVariable UUID id) {
        return demandeStageService.obtenir(id);
    }
}