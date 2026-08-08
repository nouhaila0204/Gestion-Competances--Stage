package com.sbgs.backend_2026.event;

import java.time.LocalDate;
import java.util.List;

public record StageApprouveEvent(
        String emailStagiaire, String prenomStagiaire, String nomCompletStagiaire,
        String nomService, LocalDate dateDebut, LocalDate dateFin, List<String> emailsResponsablesService
) {
}