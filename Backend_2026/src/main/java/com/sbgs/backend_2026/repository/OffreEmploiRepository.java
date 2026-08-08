package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.OffreEmploi;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OffreEmploiRepository extends JpaRepository<OffreEmploi, UUID> {
}
