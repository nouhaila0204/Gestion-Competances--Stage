package com.sbgs.backend_2026.dto.Competences;

import com.sbgs.backend_2026.entity.enums.NiveauCompetence;
import java.util.UUID;

public record CompetenceRequiseRequest(UUID competenceId, NiveauCompetence niveauRequis) {
}