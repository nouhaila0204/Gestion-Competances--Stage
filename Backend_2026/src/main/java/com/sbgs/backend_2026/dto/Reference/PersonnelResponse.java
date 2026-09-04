package com.sbgs.backend_2026.dto.Reference;
import java.util.UUID;
public record PersonnelResponse(UUID id, String nomComplet, UUID serviceId, String serviceNom) {

}
