package com.sbgs.backend_2026.dto.Stage;

import java.time.LocalDate;
import java.util.UUID;

public record AffectationCreateRequest(UUID serviceId, LocalDate dateDebut, LocalDate dateFin) {
}
