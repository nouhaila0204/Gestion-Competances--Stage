package com.sbgs.backend_2026.controller;

import com.sbgs.backend_2026.dto.Stats.StatsResponsableStageResponse;
import com.sbgs.backend_2026.service.StatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
public class StatsController {

    private final StatsService statsService;

    @GetMapping("/responsable-stage")
    public StatsResponsableStageResponse getStatsResponsableStage() {
        return statsService.getStatsResponsableStage();
    }
}