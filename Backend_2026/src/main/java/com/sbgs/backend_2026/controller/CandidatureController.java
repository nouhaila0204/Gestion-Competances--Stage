package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stage.DemandeStageCreateRequest;
import com.sbgs.backend_2026.dto.Stage.DemandeStageResponse;
import com.sbgs.backend_2026.dto.Stage.PieceJointe;
import com.sbgs.backend_2026.entity.enums.TypeDocument;
import com.sbgs.backend_2026.entity.enums.TypeStage;
import com.sbgs.backend_2026.service.CandidatureService;
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
@RequestMapping("/api/candidatures")
@RequiredArgsConstructor
public class CandidatureController {

    private final CandidatureService candidatureService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public DemandeStageResponse postuler(
            @RequestParam String nom,
            @RequestParam String prenom,
            @RequestParam String email,
            @RequestParam String telephone,
            @RequestParam String cni,
            @RequestParam String motDePasse,
            @RequestParam UUID ecoleId,
            @RequestParam String niveauEtude,
            @RequestParam String filiere,
            @RequestParam TypeStage typeStage,
            @RequestParam LocalDate dateDebutSouhaitee,
            @RequestParam String dureeSouhaitee,
            @RequestParam MultipartFile cv,
            @RequestParam MultipartFile cniFile,
            @RequestParam MultipartFile attestationAssurance,
            @RequestParam MultipartFile conventionStage,
            @RequestParam MultipartFile demandeManuscrite,
            @RequestParam List<MultipartFile> photos
    ) throws IOException {

        DemandeStageCreateRequest request = new DemandeStageCreateRequest(
                ecoleId, typeStage, dateDebutSouhaitee, dureeSouhaitee);

        List<PieceJointe> pieces = new ArrayList<>();
        pieces.add(new PieceJointe(TypeDocument.CV, cv));
        pieces.add(new PieceJointe(TypeDocument.CNI, cniFile));
        pieces.add(new PieceJointe(TypeDocument.ATTESTATION_ASSURANCE, attestationAssurance));
        pieces.add(new PieceJointe(TypeDocument.CONVENTION_STAGE, conventionStage));
        pieces.add(new PieceJointe(TypeDocument.DEMANDE_MANUSCRITE, demandeManuscrite));
        for (MultipartFile photo : photos) {
            pieces.add(new PieceJointe(TypeDocument.PHOTO, photo));
        }

        return candidatureService.postuler(nom, prenom, email,motDePasse, telephone, cni, request, pieces);
    }
}