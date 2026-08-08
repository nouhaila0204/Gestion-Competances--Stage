package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stage.Evaluation.EvaluationBatchRequest;
import com.sbgs.backend_2026.dto.Stage.Evaluation.EvaluationResponse;
import com.sbgs.backend_2026.dto.Stage.Evaluation.EvaluationUpdateRequest;
import com.sbgs.backend_2026.service.EvaluationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;

    @PostMapping("/{stageId}/evaluations")
    @ResponseStatus(HttpStatus.CREATED)
    public List<EvaluationResponse> noter(@PathVariable UUID stageId, @RequestBody EvaluationBatchRequest request) {
        return evaluationService.noter(stageId, request);
    }

    @GetMapping("/{stageId}/evaluations")
    public List<EvaluationResponse> lister(@PathVariable UUID stageId) {
        return evaluationService.lister(stageId);
    }

    @PatchMapping("/{stageId}/evaluations/{evaluationId}")
    public EvaluationResponse modifierNote(@PathVariable UUID stageId, @PathVariable UUID evaluationId,
                                           @RequestBody EvaluationUpdateRequest request) {
        return evaluationService.modifierNote(evaluationId, request);
    }
}