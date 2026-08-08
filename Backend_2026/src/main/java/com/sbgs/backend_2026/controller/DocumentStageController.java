package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stage.DocumentStageResponse;
import com.sbgs.backend_2026.entity.DocumentStage;
import com.sbgs.backend_2026.repository.DocumentStageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class DocumentStageController {

    private final DocumentStageRepository documentStageRepository;

    @GetMapping("/demandes/{demandeId}/documents")
    public List<DocumentStageResponse> lister(@PathVariable UUID demandeId) {
        return documentStageRepository.findMetadataByDemandeId(demandeId);
    }

    @GetMapping("/documents/{id}/fichier")
    public ResponseEntity<byte[]> telecharger(@PathVariable UUID id) {
        DocumentStage document = documentStageRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Document introuvable : " + id));

        MediaType type = document.getTypeContenu() != null
                ? MediaType.parseMediaType(document.getTypeContenu())
                : MediaType.APPLICATION_OCTET_STREAM;

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + document.getNomFichier() + "\"")
                .contentType(type)
                .body(document.getFichier());
    }
}
