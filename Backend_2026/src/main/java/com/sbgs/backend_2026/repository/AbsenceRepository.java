package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Absence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface AbsenceRepository extends JpaRepository<Absence, UUID> {
    List<Absence> findByStageId(UUID stageId);
    boolean existsByStageIdAndDate(UUID stageId, LocalDate date);

    // ✅ Retourne une liste de tableaux [nomService, count]
    @Query("SELECT s.nom, COUNT(a) " +
            "FROM Absence a " +
            "JOIN a.stage st " +
            "JOIN st.affectations af " +
            "JOIN af.service s " +
            "GROUP BY s.id, s.nom")
    List<Object[]> countAbsencesParServiceRaw();
}