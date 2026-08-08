package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stage.Absence.AbsenceCreateRequest;
import com.sbgs.backend_2026.dto.Stage.Absence.AbsenceResponse;
import com.sbgs.backend_2026.dto.Stage.Absence.AbsenceUpdateRequest;
import com.sbgs.backend_2026.service.AbsenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/stages")
@RequiredArgsConstructor
public class AbsenceController {

    private final AbsenceService absenceService;

    @PostMapping("/{stageId}/absences")
    @ResponseStatus(HttpStatus.CREATED)
    public AbsenceResponse marquer(@PathVariable UUID stageId, @RequestBody AbsenceCreateRequest request) {
        return absenceService.marquer(stageId, request);
    }

    @GetMapping("/{stageId}/absences")
    public List<AbsenceResponse> lister(@PathVariable UUID stageId) {
        return absenceService.lister(stageId);
    }

    @PatchMapping("/{stageId}/absences/{absenceId}")
    public AbsenceResponse modifier(@PathVariable UUID stageId, @PathVariable UUID absenceId,
                                    @RequestBody AbsenceUpdateRequest request) {
        return absenceService.modifier(absenceId, request);
    }

    @DeleteMapping("/{stageId}/absences/{absenceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void supprimer(@PathVariable UUID stageId, @PathVariable UUID absenceId) {
        absenceService.supprimer(absenceId);
    }
}