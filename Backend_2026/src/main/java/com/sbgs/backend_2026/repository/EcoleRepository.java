package com.sbgs.backend_2026.repository;

import com.sbgs.backend_2026.entity.Ecole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EcoleRepository extends JpaRepository<Ecole, UUID> {
}
