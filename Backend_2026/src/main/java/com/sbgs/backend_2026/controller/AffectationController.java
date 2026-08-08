package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stage.AffectationCreateRequest;
import com.sbgs.backend_2026.dto.Stage.AffectationResponse;
import com.sbgs.backend_2026.service.AffectationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class AffectationController {

    private final AffectationService affectationService;

    @PostMapping("/{stageId}/affectations")
    @ResponseStatus(HttpStatus.CREATED)
    public AffectationResponse affecter(@PathVariable UUID stageId,
                                        @RequestBody AffectationCreateRequest request) {
        return affectationService.affecter(stageId, request);
    }
}