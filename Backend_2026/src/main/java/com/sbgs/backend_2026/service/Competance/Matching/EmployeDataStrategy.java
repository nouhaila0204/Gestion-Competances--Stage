package com.sbgs.backend_2026.service.Matching;

import com.sbgs.backend_2026.entity.EmployeCompetence;
import com.sbgs.backend_2026.repository.EmployeCompetenceRepository;
import com.sbgs.backend_2026.service.Competance.Matching.CandidatDataSource;
import com.sbgs.backend_2026.service.Competance.Matching.ProfilCompetences;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EmployeDataStrategy implements CandidatDataSource {

    private final EmployeCompetenceRepository employeCompetenceRepository;

    @Override
    public ProfilCompetences collecterDonnees(UUID candidatId) {
        var niveaux = employeCompetenceRepository.findByEmployeId(candidatId).stream()
                .collect(Collectors.toMap(ec -> ec.getCompetence().getId(), EmployeCompetence::getNiveau));
        return new ProfilCompetences(niveaux, Set.of(), Set.of());
    }
}