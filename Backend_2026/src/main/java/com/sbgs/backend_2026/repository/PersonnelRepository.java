package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Personnel;
import com.sbgs.backend_2026.entity.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PersonnelRepository extends JpaRepository<Personnel, UUID> {
    List<Personnel> findByUtilisateur_Role(Role role);
}