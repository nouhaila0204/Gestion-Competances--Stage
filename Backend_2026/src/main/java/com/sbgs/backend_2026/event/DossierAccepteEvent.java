package com.sbgs.backend_2026.event;

import java.util.List;
import java.util.UUID;

public record DossierAccepteEvent(
        String emailStagiaire,
        String prenomStagiaire,
        UUID demandeId,
        List<String> emailsDirecteurRh
) {
}