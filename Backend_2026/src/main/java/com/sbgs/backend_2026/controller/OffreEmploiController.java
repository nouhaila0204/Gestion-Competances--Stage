package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Competences.CandidatDetailResponse;
import com.sbgs.backend_2026.dto.Competences.MatchingResultResponse;
import com.sbgs.backend_2026.dto.Competences.OffreEmploiCreateRequest;
import com.sbgs.backend_2026.dto.Competences.OffreEmploiResponse;
import com.sbgs.backend_2026.service.Competance.MatchingService;
import com.sbgs.backend_2026.service.Competance.OffreEmploiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/offres")
@RequiredArgsConstructor
public class OffreEmploiController {

    private final OffreEmploiService offreEmploiService;
    private final MatchingService matchingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OffreEmploiResponse creer(@RequestBody OffreEmploiCreateRequest request) {
        return offreEmploiService.creer(request);
    }

    @GetMapping
    public List<OffreEmploiResponse> lister() {
        return offreEmploiService.lister();
    }

    @GetMapping("/{id}")
    public OffreEmploiResponse obtenir(@PathVariable UUID id) {
        return offreEmploiService.obtenir(id);
    }

    @GetMapping("/{id}/matching")
    public MatchingResultResponse matcher(@PathVariable UUID id) {
        return matchingService.matcher(id);
    }

    @GetMapping("/{id}/matching/{type}/{candidatId}/details")
    public CandidatDetailResponse details(@PathVariable UUID id, @PathVariable String type, @PathVariable UUID candidatId) {
        return matchingService.details(id, type, candidatId);
    }
}