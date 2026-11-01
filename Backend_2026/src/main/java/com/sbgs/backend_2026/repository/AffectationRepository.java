package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Affectation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AffectationRepository extends JpaRepository<Affectation, UUID> {

    // ✅ Retourne une liste de tableaux [nomService, count] au lieu de map
    @Query("SELECT s.nom, COUNT(DISTINCT a.stage.demandeStage.stagiaire.id) " +
            "FROM Affectation a " +
            "JOIN a.service s " +
            "GROUP BY s.id, s.nom")
    List<Object[]> countStagiairesParServiceRaw();
}
