package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.DemandeStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface DemandeStageRepository extends JpaRepository<DemandeStage, UUID> {
    @Query("SELECT d FROM DemandeStage d " +
            "JOIN FETCH d.stagiaire s " +
            "JOIN FETCH s.utilisateur u " +
            "LEFT JOIN FETCH d.stage " +
            "WHERE u.email = :email " +
            "ORDER BY d.dateSoumission DESC")
    List<DemandeStage> findAllByStagiaireEmailWithStage(@Param("email") String email);
}
