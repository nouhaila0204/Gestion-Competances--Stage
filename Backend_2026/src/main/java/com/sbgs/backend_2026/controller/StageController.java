package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stage.StageResponse;
import com.sbgs.backend_2026.entity.Stage;
import com.sbgs.backend_2026.service.StageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class StageController {

    private final StageService stageService;

    @GetMapping("/{id}")
    public StageResponse obtenir(@PathVariable UUID id) {
        return stageService.obtenir(id);
    }

    @GetMapping
    public List<StageResponse> lister(@RequestParam(required = false) UUID responsableStageId) {
        return stageService.lister(responsableStageId);
    }

    @PostMapping(value = "/{id}/rapport", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StageResponse deposerRapport(@PathVariable UUID id,
                                        @RequestParam String themeStage,
                                        @RequestParam("fichier") MultipartFile fichier) throws IOException {
        return stageService.deposerRapport(id, themeStage, fichier);
    }

    @GetMapping("/{id}/rapport")
    public ResponseEntity<byte[]> telechargerRapport(@PathVariable UUID id) {
        Stage stage = stageService.obtenirEntite(id);
        if (stage.getRapportFichier() == null) {
            throw new IllegalArgumentException("Aucun rapport depose pour ce stage : " + id);
        }
        MediaType type = stage.getRapportTypeContenu() != null
                ? MediaType.parseMediaType(stage.getRapportTypeContenu()) : MediaType.APPLICATION_OCTET_STREAM;
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + stage.getRapportNomFichier() + "\"")
                .contentType(type)
                .body(stage.getRapportFichier());
    }

    @PostMapping("/{id}/valider")
    public StageResponse validerFinStage(@PathVariable UUID id) {
        return stageService.validerFinStage(id);
    }

    @PatchMapping("/{id}/attestation-remise")
    public StageResponse marquerAttestationRemise(@PathVariable UUID id) {
        return stageService.marquerAttestationRemise(id);
    }

    @GetMapping("/{id}/attestation")
    public ResponseEntity<byte[]> telechargerAttestation(@PathVariable UUID id) {
        Stage stage = stageService.obtenirEntite(id);
        if (stage.getAttestationFichier() == null) {
            throw new IllegalArgumentException("Attestation pas encore generee pour ce stage : " + id);
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + stage.getAttestationNomFichier() + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stage.getAttestationFichier());
    }
}