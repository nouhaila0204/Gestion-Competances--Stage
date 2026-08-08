package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, UUID> {
}
